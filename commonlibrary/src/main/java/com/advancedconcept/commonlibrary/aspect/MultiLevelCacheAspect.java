package com.advancedconcept.commonlibrary.aspect;

import com.advancedconcept.commonlibrary.annotation.MultiLevelCacheable;
import com.advancedconcept.commonlibrary.core.CacheValueWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class MultiLevelCacheAspect {

    private final Executor cacheRefreshExecutor;
    private final CacheManager caffeineCacheManager;
    private final RedissonClient redissonClient;

    private final ExpressionParser parser = new SpelExpressionParser();

    public MultiLevelCacheAspect(
            CacheManager caffeineCacheManager,
            RedissonClient redissonClient,
            @Qualifier("cacheRefreshExecutor") Executor cacheRefreshExecutor
    ) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redissonClient = redissonClient;
        this.cacheRefreshExecutor = cacheRefreshExecutor;
    }

    @Around("@annotation(multiLevelCacheable)")
    public Object handleMultiLevelCache(ProceedingJoinPoint joinPoint, MultiLevelCacheable multiLevelCacheable) throws Throwable {
        String cacheName = multiLevelCacheable.name();
        String generatedKey = parseKey(multiLevelCacheable.key(), joinPoint);
        String fullCacheKey = cacheName + ":" + generatedKey;

        // 1. Check L1 Cache (Caffeine)
        Cache l1Cache = caffeineCacheManager.getCache(cacheName);
        if (l1Cache != null) {
            Cache.ValueWrapper l1ValueWrapper = l1Cache.get(generatedKey);
            if (l1ValueWrapper != null && l1ValueWrapper.get() instanceof CacheValueWrapper cachedData) {

                long l1TtlMillis = multiLevelCacheable.timeUnit().toMillis(multiLevelCacheable.l1Ttl());
                long elapsedTime = System.currentTimeMillis() - cachedData.getCreatedAt();
                double usedRatio = (double) elapsedTime / l1TtlMillis;

                // Kiểm tra xem L1 đã chạm ngưỡng >= 80% TTL hay chưa
                if (usedRatio >= 0.8) {
                    log.info("[L1 HIT - REFRESH AHEAD] Key: {} (L1 Used: {}%). Trả kết quả L1 & Refresh ngầm...",
                            fullCacheKey, Math.round(usedRatio * 100));

                    // Trigger Async Refresh ngầm (vẫn lấy L2/DB)
                    triggerAsyncL1Refresh(joinPoint, multiLevelCacheable, fullCacheKey, generatedKey, l1Cache);
                } else {
                    log.info("[L1 HIT] Key: {}", fullCacheKey);
                }

                // Luôn trả về dữ liệu L1 hiện tại ngay lập tức
                return cachedData.getValue();
            }
        }
        log.info("[L1 MISS] Key: {}", fullCacheKey);

        // 2. Check L2 Cache (Redis)
        return fetchFromL2OrDb(joinPoint, multiLevelCacheable, fullCacheKey, generatedKey, l1Cache);
    }

    private void triggerAsyncL1Refresh(ProceedingJoinPoint joinPoint,
                                       MultiLevelCacheable multiLevelCacheable,
                                       String fullCacheKey,
                                       String generatedKey,
                                       Cache l1Cache) {
        cacheRefreshExecutor.execute(() -> {

            log.info("[ASYNC REFRESH START] Đang kiểm tra L2 / DB để làm mới L1 key: {}", fullCacheKey);

            // Mò xuống L2 xem L2 có data tươi không
            RBucket<Object> l2Bucket = redissonClient.getBucket(fullCacheKey);
            Object l2Value = l2Bucket.get();
            if (l2Value != null) {
                log.info("[ASYNC REFRESH - L2 HIT] Refresh L1 từ L2 cho key: {}", fullCacheKey);
                putToL1(l1Cache, generatedKey, l2Value);
                return;
            }

            log.info("[ASYNC REFRESH - L2 MISS] Down DB để refresh L1 & L2 cho key: {}", fullCacheKey);

            String lockKey = "lock:refresh:l1:" + fullCacheKey;
            RLock lock = redissonClient.getLock(lockKey);

            // tryLock(0) -> Nếu có Thread khác đang refresh key này rồi thì bỏ qua ngay
            try {
                boolean acquired = lock.tryLock(0, 10, TimeUnit.SECONDS);
                if (!acquired) {
                    log.info("[ASYNC REFRESH SKIPPED] Process khác đang refresh L1 cho key: {}", fullCacheKey);
                    return;
                }

                // DOUBLE CHECK L2
                l2Value = l2Bucket.get();
                if (l2Value != null) {
                    log.info("[ASYNC REFRESH - DOUBLE CHECK L2 HIT]: {}", fullCacheKey);
                    putToL1(l1Cache, generatedKey, l2Value);
                    return;
                }

                // 4. L2 thật sự MISS -> DB
                log.info("[ASYNC REFRESH - DOUBLE CHECK L2 MISS] Fetch DB: {}", fullCacheKey);
                Object dbResult = joinPoint.proceed();

                if (dbResult != null) {
                    l2Bucket.set(dbResult, Duration.of(multiLevelCacheable.l2Ttl(), multiLevelCacheable.timeUnit().toChronoUnit()));
                    putToL1(l1Cache, generatedKey, dbResult);
                    log.info("[ASYNC REFRESH SUCCESS] Cập nhật xong L1 & L2 cho key: {}", fullCacheKey);
                }
            } catch (Throwable e) {
                log.error("[ASYNC REFRESH ERROR] Lỗi khi refresh ngầm key: {}", fullCacheKey, e);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });
    }

    private Object fetchFromL2OrDb(ProceedingJoinPoint joinPoint,
                                   MultiLevelCacheable multiLevelCacheable,
                                   String fullCacheKey,
                                   String generatedKey,
                                   Cache l1Cache) throws Throwable {
        RBucket<Object> l2Bucket = redissonClient.getBucket(fullCacheKey);
        Object l2Value = l2Bucket.get();
        if (l2Value != null) {
            log.info("[L2 HIT] Key: {}. Updating L1...", fullCacheKey);
            putToL1(l1Cache, generatedKey, l2Value);
            return l2Value;
        }
        log.info("[L2 MISS] Key: {}", fullCacheKey);

        // 3. Cả L1 và L2 đều MISS -> Khóa Distributed Lock để query Database
        String lockKey = "lock:" + fullCacheKey;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Try to acquire the lock in 5s, lock will be released after 10s
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("Could not acquire lock for key: {}. Retrying L2 fetch...", lockKey);
                // Wait for a short term and try to get from L2 (avoid stampede)
                Thread.sleep(100);
                Object retryL2 = l2Bucket.get();
                if (retryL2 != null) {
                    putToL1(l1Cache, generatedKey, retryL2);
                    return retryL2;
                }

                log.error("[CACHE LOAD FAILED] Lock unavailable and L2 still MISS for key: {}", fullCacheKey);
                return null;
            }

            // DOUBLE CHECK LOCK PATTERN: after acquiring lock, check L2 again
            Object doubleCheckL2 = l2Bucket.get();
            if (doubleCheckL2 != null) {
                log.info("[L2 DOUBLE-CHECK HIT] Key: {}", fullCacheKey);
                putToL1(l1Cache, generatedKey, doubleCheckL2);
                return doubleCheckL2;
            }

            // Go to Database to get data
            log.info("[DB FETCH] Fetching data from Database for key: {}", fullCacheKey);
            Object dbResult = joinPoint.proceed();

            if (dbResult != null) {
                // Update L2 (Redis)
                l2Bucket.set(dbResult, Duration.of(multiLevelCacheable.l2Ttl(), multiLevelCacheable.timeUnit().toChronoUnit()));
                // Update L1 (Caffeine)
                putToL1(l1Cache, generatedKey, dbResult);
                log.info("[CACHE UPDATED] Successfully updated L1 and L2 for key: {}", fullCacheKey);
            }

            return dbResult;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void putToL1(Cache l1Cache, String key, Object value) {
        if (l1Cache != null) {
            l1Cache.put(key, new CacheValueWrapper(value));
        }
    }

    private String parseKey(String spelExpression, ProceedingJoinPoint joinPoint) {
        if (spelExpression == null || spelExpression.isBlank()) {
            return "default";
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = signature.getParameterNames();

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        return parser.parseExpression(spelExpression).getValue(context, String.class);
    }
}
