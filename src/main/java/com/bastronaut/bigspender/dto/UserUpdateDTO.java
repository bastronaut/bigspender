package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateDTO {
    private final String name;
    private final String password;
    private final String email;
}
