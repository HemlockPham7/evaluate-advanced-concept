package com.advancedconcept.commonlibrary.dto.record;

import java.io.Serializable;

public record UserResponse (
        Long id,
        String name,
        String address,
        String phone,
        String email,
        String username,
        String cccd
) implements Serializable {
}
