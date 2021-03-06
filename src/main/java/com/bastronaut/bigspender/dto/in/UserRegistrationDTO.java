package com.bastronaut.bigspender.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_INVALID_EMAIL;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_USER_EMAIL_NULL;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_USER_PW_NULL;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_USER_PW_SIZE;
import static com.bastronaut.bigspender.utils.ApplicationConstants.PASSWORDMINSIZE;

@Getter
@AllArgsConstructor
public class UserRegistrationDTO {

    @NotEmpty(message = ERRORMSG_USER_EMAIL_NULL)
    @Email(message = ERRORMSG_INVALID_EMAIL)
    private final String email;

    @NotEmpty(message = ERRORMSG_USER_PW_NULL)
    @Size(min=PASSWORDMINSIZE, message = ERRORMSG_USER_PW_SIZE)
    private final String password;
}
