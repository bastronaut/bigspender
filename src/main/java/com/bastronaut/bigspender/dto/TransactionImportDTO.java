package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;



@Getter
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
        this.user = UserDTO.fromUser(user);
    }

}
