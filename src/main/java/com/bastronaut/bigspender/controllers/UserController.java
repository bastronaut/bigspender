package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.dto.UserRegistrationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;

import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_LOGIN_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    @PostMapping(path = USERS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@Valid UserRegistrationDTO user, BindingResult result) {
        System.out.println("test");
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = USERS_LOGIN_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity login(Principal principal) {
        System.out.println(principal.getName());
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = USERS_LOGIN_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity testy(Principal principal) {
        System.out.println(principal.getName());
        return new ResponseEntity(HttpStatus.OK);
    }



}
