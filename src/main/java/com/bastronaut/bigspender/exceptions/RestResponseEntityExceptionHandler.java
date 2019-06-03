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

import static com.bastronaut.bigspender.utils.ApplicationConstants.REGISTRATION_ERROR_MSG;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_ERROR_MSG;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_IMPORT_ERROR_MSG;
import static com.bastronaut.bigspender.utils.ApplicationConstants.UPDATE_ERROR_MSG;

@ControllerAdvice
@RestController
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {



    @ExceptionHandler(value = {UserRegistrationException.class})
    public final ResponseEntity<ErrorDetails> handleUserRegistrationException(UserRegistrationException ex, WebRequest request) {
    final ErrorDetails errorDetails = new ErrorDetails(REGISTRATION_ERROR_MSG, ex.getMessage(), ex.getErrors(), LocalDate.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {UserUpdateException.class})
    public final ResponseEntity<ErrorDetails> handleUserUpdateException(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(UPDATE_ERROR_MSG, ex.getMessage(), null, LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {TransactionImportException.class})
    public final ResponseEntity<ErrorDetails> handleInvalidTransactionImport(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(TRANSACTION_IMPORT_ERROR_MSG, ex.getMessage(), null, LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }


    @ExceptionHandler(value = {TransactionException.class})
    public final ResponseEntity<ErrorDetails> handleInvalidTransaction(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(TRANSACTION_ERROR_MSG, ex.getMessage(), null, LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
}
