package com.advancedconcept.commonlibrary.dto.record;

public record UserRequest (
        String name,
        String address,
        String phone,
        String email,
        String username,
        String password
) {
}
