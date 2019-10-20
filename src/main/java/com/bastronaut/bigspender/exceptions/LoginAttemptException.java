package com.bastronaut.bigspender.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class LoginAttemptException extends RuntimeException {
    public LoginAttemptException(String message) { super(message); }
}
