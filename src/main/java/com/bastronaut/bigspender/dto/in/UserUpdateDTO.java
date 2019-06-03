package com.bastronaut.bigspender.dto.in;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateDTO {

    private final String password;
    private final String email;
}
