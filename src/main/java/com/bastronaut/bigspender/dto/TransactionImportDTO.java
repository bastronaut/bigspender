package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;



public class TransactionImportDTO {

    private final List<Transaction> transactions;
    private final LocalDate importDate;
    private final int importCount;
    private final UserDTO user;

    public TransactionImportDTO(TransactionImport transactionImport) {
        this.transactions = transactionImport.getTransactions();
        this.importDate = transactionImport.getImportDate();
        this.importCount = transactionImport.getImportCount();
        final User user = transactionImport.getUser();
        this.user = newUser(user);
    }

    public UserDTO newUser(User user) {
        return new UserDTO(user.getFirstName(), user.getEmail(), user.getPassword());
    }

}
