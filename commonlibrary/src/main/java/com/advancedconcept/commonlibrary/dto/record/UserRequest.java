package com.advancedconcept.commonlibrary.dto.record;

import com.advancedconcept.commonlibrary.annotation.Cccd;
import jakarta.validation.constraints.*;

public record UserRequest (
        @NotBlank(message = "Name field cannot be blank")
        String name,
        String address,
        @Min(value = 0, message = "Value cannot be smaller than 0")
        @Size(min = 9, max = 11, message = "Phone should have between 9 to 11 characters")
        String phone,
        @Email
        @NotBlank
        String email,
        String username,
        String password,
        @Cccd
        String cccd
) {
}
