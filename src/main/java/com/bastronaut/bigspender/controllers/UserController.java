package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.dto.UserLoginDTO;
import com.bastronaut.bigspender.dto.UserRegistrationDTO;
import com.bastronaut.bigspender.dto.UserUpdateDTO;
import com.bastronaut.bigspender.exceptions.RegistrationException;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<UserDTO> createUser(@Valid final UserRegistrationDTO userRegistrationDTO,
                                              final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RegistrationException(bindingResult.toString());
        }
        final User user = User.fromUserRegistrationDTO(userRegistrationDTO);
        final User registeredUser = userDetailsService.registerUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(registeredUser));
    }

    // TODO:
    // https://www.devglan.com/spring-security/spring-boot-security-rest-basic-authentication
//    @PostMapping(path = USERS_LOGIN_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
//    public ResponseEntity login(@Valid final UserLoginDTO login, final BindingResult bindingResult) {
//
//
//        return new ResponseEntity(HttpStatus.BAD_REQUEST);
//    }

    @GetMapping(path = USERS_LOGIN_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity testy(final Principal principal, final HttpServletRequest request) {
        System.out.println(request.getHeaderNames());
        System.out.println(principal.getName());
        final User user = userDetailsService.loadUserByUsername(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(user));
    }

    @PutMapping(path = "/users/yolo", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(final @AuthenticationPrincipal User activeUser, final UserUpdateDTO userUpdateDTO) {
        final User updateUser = User.fromUserUpdateDTO(userUpdateDTO);
        final User result = userDetailsService.updateUser(activeUser.getId(), updateUser);
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(result));



    }



}
