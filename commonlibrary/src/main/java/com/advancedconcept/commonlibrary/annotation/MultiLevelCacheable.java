package com.advancedconcept.commonlibrary.annotation;

import java.lang.annotation.ElementType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLevelCacheable {
    String name();

    String group() default "";

    String key() default "";

    long l1Ttl() default 150;

    long l2Ttl() default 1800;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
