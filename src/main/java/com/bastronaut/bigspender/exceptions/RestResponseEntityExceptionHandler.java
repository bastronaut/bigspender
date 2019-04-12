package com.bastronaut.bigspender.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RegistrationException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException exception, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails("Registration ExceptioN", exception.getMessage(), LocalDate.now());
        return handleExceptionInternal(exception, errorDetails, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
