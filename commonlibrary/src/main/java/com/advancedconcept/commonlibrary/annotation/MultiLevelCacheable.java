package com.advancedconcept.commonlibrary.annotation;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLevelCacheable {
    /**
     * Tên vùng cache (ví dụ: "products")
     */
    String name();

    /**
     * Key SpEL expression (ví dụ: "#id" hoặc "'all:' + #name + ':' + #pageable.pageNumber")
     */
    String key() default "";

    /**
     * Thời gian sống ở L1 Cache (Caffeine)
     */
    long l1Ttl() default 240;

    /**
     * Thời gian sống ở L2 Cache (Redis)
     */
    long l2Ttl() default 1800;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
