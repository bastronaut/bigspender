package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserDTO {

    private final String name;
    private final String email;
    private long id;

    public UserDTO(final String name, final String email, final long id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public static UserDTO fromUser(final User user) {
        return new UserDTO(user.getName(), user.getEmail(), user.getId());
    }

}
