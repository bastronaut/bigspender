package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public class TransactionImportDTO {

    private final List<TransactionDTO> transactions;
    private final LocalDate importDate;
    private final int importCount;
    private final UserDTO user;

    public TransactionImportDTO(TransactionImport transactionImport) {
        final List<Transaction> txs = transactionImport.getTransactions();

        this.transactions = txs.stream().map(TransactionDTO::fromTransaction).collect(Collectors.toList());
        this.importDate = transactionImport.getImportDate();
        this.importCount = transactionImport.getImportCount();
        final User user = transactionImport.getUser();
        this.user = UserDTO.fromUser(user);
    }

}
