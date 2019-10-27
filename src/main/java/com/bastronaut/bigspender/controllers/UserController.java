package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.out.UserDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Optional;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_REGISTRATION_NOT_ALLOWED;
import static com.bastronaut.bigspender.utils.ApplicationConstants.INVALID_UPDATE_INFORMATION;
import static com.bastronaut.bigspender.utils.ApplicationConstants.LOGIN_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.USER_RESOURCE_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UserController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping(path = USERS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createUser(@Valid final UserRegistrationDTO userRegistrationDTO,
                                              final @AuthenticationPrincipal User activeUser,
                                              final BindingResult bindingResult,
                                              final HttpServletRequest request) throws ServletException {
        // Don't allow registration while user is logged in
        if (activeUser != null) {
            throw new UserRegistrationException(ERRORMSG_REGISTRATION_NOT_ALLOWED);
        }

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
    @PutMapping(path = USER_RESOURCE_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(final @AuthenticationPrincipal User activeUser,
                                              @Valid final UserUpdateDTO userUpdateDTO,
                                              final BindingResult bindingResult,
                                              final @PathVariable String userid) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0);
            throw new UserUpdateException(error.getDefaultMessage());
        }

        if (StringUtils.isEmpty(userUpdateDTO.getEmail()) && StringUtils.isEmpty(userUpdateDTO.getPassword())) {
            throw new UserUpdateException(INVALID_UPDATE_INFORMATION);
        }

        final User result = userDetailsService.updateUser(activeUser, userUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(result));
    }

    @GetMapping(path = USER_RESOURCE_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUser(final @AuthenticationPrincipal User activeUser) {
        User user = userDetailsService.loadUserByUsername(activeUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(UserDTO.fromUser(user));
    }

    @GetMapping(path = LOGIN_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity login(final @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
