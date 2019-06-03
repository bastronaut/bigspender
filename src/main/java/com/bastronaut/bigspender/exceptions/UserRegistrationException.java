package com.bastronaut.bigspender.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserRegistrationException extends RuntimeException {

    @Getter
    final List<String> errors;

    public UserRegistrationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
