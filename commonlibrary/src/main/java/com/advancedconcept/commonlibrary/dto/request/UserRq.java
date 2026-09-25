package com.advancedconcept.commonlibrary.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRq {
    private String name;
    private String address;
    private String phone;
    private String email;
    private String username;
    private String password;
}
