package com.bastronaut.bigspender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginDTO {
    private String password;
    private String username;
}
