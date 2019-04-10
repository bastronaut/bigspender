package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserDTO {

    private final String name;
    private final String email;
    private long id;
    private final String password;

    public UserDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getName(), user.getEmail(), user.getPassword());
    }

}
