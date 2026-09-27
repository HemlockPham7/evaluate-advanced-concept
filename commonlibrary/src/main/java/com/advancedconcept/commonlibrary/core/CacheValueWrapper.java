package com.advancedconcept.commonlibrary.core;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class CacheValueWrapper implements Serializable {

    private final Object value;
    private final long createdAt;

    public CacheValueWrapper(Object value) {
        this.value = value;
        this.createdAt = System.currentTimeMillis();
    }

}
