package com.advancedconcept.commonlibrary.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MultiCacheConfig {

    @Bean
    public CacheManager caffeineCacheManger() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(5000)
                .expireAfterWrite(240, TimeUnit.SECONDS)); // Default TTL cho L1
        return cacheManager;
    }
}
