package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDTO {

    private final String email;
    private long id;

    public static UserDTO fromUser(final User user) {
        return new UserDTO(user.getEmail(), user.getId());
    }

}
