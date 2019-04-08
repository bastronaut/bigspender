package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegistrationDTO {
    private final String firstName;
    private final String email;
    private final String password;
}
