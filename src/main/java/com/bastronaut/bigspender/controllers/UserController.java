package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.dto.UserRegistrationDTO;
import com.bastronaut.bigspender.dto.UserUpdateDTO;
import com.bastronaut.bigspender.exceptions.UserRegistrationException;
import com.bastronaut.bigspender.exceptions.UserUpdateException;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.security.Principal;

import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_UPDATE_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @PostMapping(path = USERS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createUser(@Valid final UserRegistrationDTO userRegistrationDTO,
                                              final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UserRegistrationException(bindingResult.toString());
        }
        final User user = User.fromUserRegistrationDTO(userRegistrationDTO);
        final User registeredUser = userDetailsService.registerUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(registeredUser));
    }

    /**
     * Ignores the userid for now.. only allows updating the Authenticated Principal based on the session.
     * Does not allow updating other user accounts obviously. Think about throwing 403 or just doing the update
     * for the principal?
     * @param activeUser the authenticated user in session
     * @param userUpdateDTO the update information sent in POST data
     * @param userid the user resource ID
     * @return
     */
    @PutMapping(path = USERS_UPDATE_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(final @AuthenticationPrincipal User activeUser, final UserUpdateDTO userUpdateDTO,
                                              final @PathVariable String userid) {
        final User updateUser = User.fromUserUpdateDTO(userUpdateDTO);
        final User result = userDetailsService.updateUser(activeUser.getId(), updateUser);
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(result));
    }

}
