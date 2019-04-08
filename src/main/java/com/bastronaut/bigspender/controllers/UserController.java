package com.bastronaut.bigspender.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_LOGIN_ENDPOINT;

@RestController
public class UserController {

    @PostMapping(path = USERS_ENDPOINT)
    public ResponseEntity createUser() {
        String yo = "123";
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = USERS_LOGIN_ENDPOINT)
    public ResponseEntity login() {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

}
