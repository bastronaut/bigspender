package com.bastronaut.bigspender.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
    private static final String USER_UPDATE_ERROR_MSG = "User update error";
    private static final String TRANSACTION_IMPORT_ERROR_MSG = "Transaction Import error";
    private static final String TRANSACTION_ERROR_MSG = "Transaction error";
    private static final String LABEL_ERROR_MSG = "Label error";
    private static final String LOGIN_ERROR_MSG = "Login error";


    @ExceptionHandler(value = {UserRegistrationException.class})
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) {
    final ErrorDetails errorDetails = new ErrorDetails(REGISTRATION_ERROR_MSG, ex.getMessage(), LocalDate.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {UserUpdateException.class})
    public final ResponseEntity<ErrorDetails> handleUserUpdateException(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(USER_UPDATE_ERROR_MSG, ex.getMessage(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {TransactionImportException.class})
    public final ResponseEntity<ErrorDetails> handleInvalidTransactionImport(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(TRANSACTION_IMPORT_ERROR_MSG, ex.getMessage(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {TransactionException.class})
    public final ResponseEntity<ErrorDetails> handleInvalidTransaction(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(TRANSACTION_ERROR_MSG, ex.getMessage(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {LabelException.class})
    public final ResponseEntity<ErrorDetails> handleInvalidLabels(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LABEL_ERROR_MSG, ex.getMessage(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {LinkLabelException.class})
    public final ResponseEntity<ErrorDetails> handleInvalidLinks(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LABEL_ERROR_MSG, ex.getMessage(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(value = {LoginAttemptException.class})
    public final ResponseEntity<ErrorDetails> handleTooManyLogins(Exception ex, WebRequest request) {
        final ErrorDetails errorDetails = new ErrorDetails(LOGIN_ERROR_MSG , ex.getMessage(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }

    @ExceptionHandler(value =  InternalAuthenticationServiceException.class)
    public ResponseEntity<String> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        ResponseEntity<String> response = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return response;
    }
}
