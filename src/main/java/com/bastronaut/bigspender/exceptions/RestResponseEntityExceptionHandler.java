package com.bastronaut.bigspender.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
@RestController
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String REGISTRATION_ERROR_MSG = "Registration error";
    private static final String UPDATE_ERROR_MSG = "User update error";

    @ExceptionHandler(value = {UserRegistrationException.class})
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) {
    final ErrorDetails errorDetails = new ErrorDetails(REGISTRATION_ERROR_MSG, ex.getMessage(), LocalDate.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {UserUpdateException.class})
    public final ResponseEntity<ErrorDetails> handleUserUpdateException(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(UPDATE_ERROR_MSG, ex.getMessage(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

}
