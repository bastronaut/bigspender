package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.dto.in.UserRegistrationDTO;
import com.bastronaut.bigspender.dto.in.UserUpdateDTO;
import com.bastronaut.bigspender.exceptions.UserRegistrationException;
import com.bastronaut.bigspender.exceptions.UserUpdateException;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.CustomUserDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.lang.reflect.Field;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_UPDATE_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    private static final String INVALID_UPDATE_INFORMATION = "No correct updateable information provided";

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping(path = USERS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createUser(@Valid final UserRegistrationDTO userRegistrationDTO,
                                              final BindingResult bindingResult,
                                              final HttpServletRequest request) throws ServletException {
        // TODO:
        //  Ensure no existing session exists. Alternatively, we could reject login if session exists

        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0); // For now handle errors individually
            throw new UserRegistrationException(error.getDefaultMessage());
        }
        final User user = User.fromUserRegistrationDTO(userRegistrationDTO);
        final User registeredUser = userDetailsService.registerUser(user);

        userDetailsService.logUserIn(request, user.getUsername(), user.getPassword());
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
    public ResponseEntity<UserDTO> updateUser(final @AuthenticationPrincipal User activeUser,
                                              @Valid final UserUpdateDTO userUpdateDTO,
                                              final BindingResult bindingResult,
                                              final @PathVariable String userid) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0); // For now handle errors individually
            throw new UserRegistrationException(error.getDefaultMessage());
        }

        // TODO remove this logic once custom validator for @Valid userUpdateDTO is written
        if (isInvalidUserUpdateDTO(userUpdateDTO)) {
            throw new UserUpdateException(INVALID_UPDATE_INFORMATION);
        }
        final User updateUser = User.fromUserUpdateDTO(userUpdateDTO);
        final User result = userDetailsService.updateUser(activeUser, updateUser);
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(result));
    }

    /**
     * Tests if at minimum, 1 updateable field is provided
     * TODO can be replaced with validators on controller model
     * @param userUpdateDTO the user update information provided in the request
     * @return true if no updateable field is provided
     */
    private boolean isInvalidUserUpdateDTO(final UserUpdateDTO userUpdateDTO) {
        return StringUtils.isAllBlank(userUpdateDTO.getEmail(), userUpdateDTO.getPassword());
    }

}
