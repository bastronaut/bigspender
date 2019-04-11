package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.dto.UserRegistrationDTO;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.security.Principal;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_LOGIN_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @PostMapping(path = USERS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@Valid UserRegistrationDTO userDTO, BindingResult result) {

        // ok this is super funky gotta rework this
        User user = User.fromUserRegistrationDTO(userDTO);
        Optional<User> registeredUser = userDetailsService.registerUser(user);
        if (registeredUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(registeredUser.get());
        }
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
