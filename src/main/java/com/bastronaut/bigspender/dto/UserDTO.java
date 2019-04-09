package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserDTO {

    private final String firstName;
    private final String email;
    private long id;
    private final String password;

    public UserDTO(User user) {
        this.firstName = user.getFirstName();
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    public UserDTO(String firstName, String email, String password) {
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }

}
