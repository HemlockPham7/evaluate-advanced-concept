package com.advancedconcept.commonlibrary.aspect;

import com.advancedconcept.commonlibrary.annotation.MultiLevelCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MultiLevelCacheAspect {

    private final CacheManager caffeineCacheManager;
    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(multiLevelCacheable)")
    public Object handleMultiLevelCache(ProceedingJoinPoint joinPoint, MultiLevelCacheable multiLevelCacheable) throws Throwable {
        String cacheName = multiLevelCacheable.name();
        String generatedKey = parseKey(multiLevelCacheable.key(), joinPoint);
        String fullCacheKey = cacheName + ":" + generatedKey;

        // 1. Kiểm tra L1 Cache (Caffeine)
        Cache l1Cache = caffeineCacheManager.getCache(cacheName);
        if (l1Cache != null) {
            Cache.ValueWrapper l1Value = l1Cache.get(generatedKey);
            if (l1Value != null && l1Value.get() != null) {
                log.info("[L1 HIT] Key: {}", fullCacheKey);
                return l1Value.get();
            }
        }
        log.info("[L1 MISS] Key: {}", fullCacheKey);

        // 2. Kiểm tra L2 Cache (Redis)
        RBucket<Object> l2Bucket = redissonClient.getBucket(fullCacheKey);
        Object l2Value = l2Bucket.get();
        if (l2Value != null) {
            log.info("[L2 HIT] Key: {}. Updating L1...", fullCacheKey);
            // Updating L1...
            if (l1Cache != null) {
                l1Cache.put(generatedKey, l2Value);
            }
            return l2Value;
        }
        log.info("[L2 MISS] Key: {}", fullCacheKey);

        // 3. Cả L1 và L2 đều MISS -> Khóa Distributed Lock để query Database
        String lockKey = "lock:" + fullCacheKey;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // Thử lấy lock trong vòng 5 giây, lock tự giải phóng sau 10 giây nếu crash
            boolean acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("Could not acquire lock for key: {}. Retrying L2 fetch...", lockKey);
                // Đợi ngắn và thử lấy lại từ L2 (tránh stampede)
                Thread.sleep(100);
                Object retryL2 = l2Bucket.get();
                if (retryL2 != null) {
                    if (l1Cache != null) l1Cache.put(generatedKey, retryL2);
                    return retryL2;
                }
            }

            // DOUBLE CHECK LOCK PATTERN: Sau khi lấy được lock, check lại L2 lần nữa
            Object doubleCheckL2 = l2Bucket.get();
            if (doubleCheckL2 != null) {
                log.info("[L2 DOUBLE-CHECK HIT] Key: {}", fullCacheKey);
                if (l1Cache != null) l1Cache.put(generatedKey, doubleCheckL2);
                return doubleCheckL2;
            }

            // Thực thi xuống Database lấy dữ liệu
            log.info("[DB FETCH] Fetching data from Database for key: {}", fullCacheKey);
            Object dbResult = joinPoint.proceed();

            if (dbResult != null) {
                // Cập nhật L2 (Redis)
                l2Bucket.set(dbResult, Duration.of(multiLevelCacheable.l2Ttl(), multiLevelCacheable.timeUnit().toChronoUnit()));

                // Cập nhật L1 (Caffeine)
                if (l1Cache != null) {
                    l1Cache.put(generatedKey, dbResult);
                }
                log.info("[CACHE UPDATED] Successfully updated L1 and L2 for key: {}", fullCacheKey);
            }

            return dbResult;

        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * Helper phân tích SpEL Expression thành Cache Key thực tế
     */
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
