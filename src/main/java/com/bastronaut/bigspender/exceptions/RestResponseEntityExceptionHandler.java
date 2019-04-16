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
    private static final String GENERIC_ERROR_MSG = "Generic error";

    /*
     TODO:
     - create new classes extending ResponseEntityExceptionHandler?
     - create some sort of if statement in handleConflict?
     */

//
//    @ExceptionHandler(value = {UserRegistrationException.class, UserUpdateException.class})
//    protected ResponseEntity<Object> handleConflict(RuntimeException exception, WebRequest request) {
//
//        final ErrorDetails errorDetails;
//
//        if (exception instanceof UserRegistrationException) {
//            errorDetails = new ErrorDetails(REGISTRATION_ERROR_MSG, exception.getMessage(), LocalDate.now());
//        } else if (exception instanceof  UserUpdateException) {
//            errorDetails = new ErrorDetails(UPDATE_ERROR_MSG, exception.getMessage(), LocalDate.now());
//        } else {
//            errorDetails = new ErrorDetails(GENERIC_ERROR_MSG, exception.getMessage(), LocalDate.now());
//        }
//        return handleExceptionInternal(exception, errorDetails, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//
//
//
//    }

        @ExceptionHandler(value = {UserRegistrationException.class, UserUpdateException.class})
        public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) {


        final ErrorDetails errorDetails;

        if (ex instanceof UserRegistrationException) {
            errorDetails = new ErrorDetails(REGISTRATION_ERROR_MSG, ex.getMessage(), LocalDate.now());
        } else if (ex instanceof  UserUpdateException) {
            errorDetails = new ErrorDetails(UPDATE_ERROR_MSG, ex.getMessage(), LocalDate.now());
        } else {
            errorDetails = new ErrorDetails(GENERIC_ERROR_MSG, ex.getMessage(), LocalDate.now());
        }
            return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }


}
