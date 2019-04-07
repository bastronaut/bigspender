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
    private final User user;

    public TransactionImportDTO(TransactionImport transactionImport) {
        this.transactions = transactionImport.getTransactions();
        this.importDate = transactionImport.getImportDate();
        this.importCount = transactionImport.getImportCount();
        this.user = transactionImport.getUser(); // TODO
    }

}
