package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;

public class UserDTO {

    private final String firstName;
    private final long id;

    public UserDTO(User user) {
        this.firstName = user.getFirstName();
        this.id = user.getId();
    }
}
