package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.Getter;

@Getter
public class UserDTO {

    private final String firstName;
    private final String email;
    private final long id;

    public UserDTO(User user) {
        this.firstName = user.getFirstName();
        this.id = user.getId();
        this.email = user.getEmail();
    }
}
