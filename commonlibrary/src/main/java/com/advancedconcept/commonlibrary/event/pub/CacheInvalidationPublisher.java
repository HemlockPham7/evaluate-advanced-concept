package com.advancedconcept.commonlibrary.event.pub;

import com.advancedconcept.commonlibrary.dto.cache.CacheInvalidateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationPublisher {

    public static final String CACHE_INVALIDATE_TOPIC = "cache:invalidate:topic";

    private final RedissonClient redissonClient;

    /**
     * Xóa key/group cụ thể ở L2 (Redis) và Broadcast cho L1 (Caffeine)
     */
    public void invalidate(String cacheName, String group, String key) {
        String fullKey = cacheName + ":" + group + ":" + key;

        // 1. Xóa L2 Redis cụ thể
        redissonClient.getBucket(fullKey).delete();
        log.info("[L2 INVALIDATE KEY] Deleted Redis key: {}", fullKey);

        // 2. Broadcast xóa L1 Caffeine cho tất cả các node
        publishInvalidateSignal(new CacheInvalidateMessage(cacheName, group, key, false));
    }

    /**
     * Evict toàn bộ group (Ví dụ: evict toàn bộ `products:list:*`)
     */
    public void invalidateGroup(String cacheName, String group) {
        String pattern = cacheName + ":" + group + ":*";

        // 1. Scan & Delete tất cả keys khớp pattern ở L2 Redis
        RKeys keys = redissonClient.getKeys();
        long deletedCount = keys.deleteByPattern(pattern);
        log.info("[L2 INVALIDATE GROUP] Deleted {} keys matching pattern: {}", deletedCount, pattern);

        // 2. Broadcast xóa L1 Group Caffeine
        publishInvalidateSignal(new CacheInvalidateMessage(cacheName, group, null, true));
    }

    private void publishInvalidateSignal(CacheInvalidateMessage message) {
        RTopic topic = redissonClient.getTopic(CACHE_INVALIDATE_TOPIC);
        topic.publish(message);
        log.info("[REDIS PUB/SUB] Published invalidate message: {}", message);
    }
}
