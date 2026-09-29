package com.advancedconcept.commonlibrary.dto.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheInvalidateMessage implements Serializable {
    private String cacheName;
    private String group;
    private String key;
    private boolean clearGroup;
}