package com.advancedconcept.commonlibrary.event.sub;

import com.advancedconcept.commonlibrary.dto.cache.CacheInvalidateMessage;
import com.advancedconcept.commonlibrary.event.pub.CacheInvalidationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class L1CacheInvalidationSubscriber {

    private final RedissonClient redissonClient;
    private final CacheManager caffeineCacheManager;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribeL1Invalidation() {
        RTopic topic = redissonClient.getTopic(CacheInvalidationPublisher.CACHE_INVALIDATE_TOPIC);

        topic.addListener(CacheInvalidateMessage.class, (channel, msg) -> {
            log.info("[L1 PUB/SUB RECEIVE] Received invalidate signal: {}", msg);

            // Tên L1 Cache Manager được cấu hình theo pattern: "name:group"
            String l1CacheName = msg.getCacheName() + ":" + msg.getGroup();
            Cache l1Cache = caffeineCacheManager.getCache(l1CacheName);

            if (l1Cache != null) {
                if (msg.isClearGroup() || msg.getKey() == null) {
                    // Evict toàn bộ group trong L1
                    l1Cache.clear();
                    log.info("[L1 CLEAR GROUP SUCCESS] Cleared L1 Cache: {}", l1CacheName);
                } else {
                    // Evict key cụ thể trong L1
                    l1Cache.evict(msg.getKey());
                    log.info("[L1 EVICT KEY SUCCESS] Evicted key: {} from L1 Cache: {}", msg.getKey(), l1CacheName);
                }
            }
        });

        log.info("[REDIS PUB/SUB] Subscribed to topic: {}", CacheInvalidationPublisher.CACHE_INVALIDATE_TOPIC);
    }
}
