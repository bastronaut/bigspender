package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_LOGIN_ENDPOINT;

@RestController
public class UserController {

    @PostMapping(path = USERS_ENDPOINT)
    public ResponseEntity createUser(@Valid UserDTO user, BindingResult result) {
        System.out.println("test");
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = USERS_LOGIN_ENDPOINT)
    public ResponseEntity login() {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

}
