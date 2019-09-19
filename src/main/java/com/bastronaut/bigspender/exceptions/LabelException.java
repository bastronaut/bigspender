package com.bastronaut.bigspender.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LabelException extends RuntimeException {

    public LabelException(String message) {
        super(message);
    }
}
