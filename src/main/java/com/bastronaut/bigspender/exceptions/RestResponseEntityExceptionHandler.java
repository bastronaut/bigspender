package com.bastronaut.bigspender.exceptions;

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
    private static final String USER_UPDATE_ERROR_MSG = "User update error";
    private static final String TRANSACTION_IMPORT_ERROR_MSG = "Transaction Import error";
    private static final String TRANSACTION_ERROR_MSG = "Transaction error";
    private static final String LABEL_ERROR_MSG = "Label error";


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
}
