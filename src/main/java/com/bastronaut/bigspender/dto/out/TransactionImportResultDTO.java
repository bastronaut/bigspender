package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class TransactionImportResultDTO {

    private final List<TransactionDTO> transactions;
    private final LocalDate importDate;
    private final int importCount;
    private final UserDTO user;

    public TransactionImportResultDTO(TransactionImport transactionImport) {
        final List<Transaction> txs = transactionImport.getTransactions();

        this.transactions = txs.stream().map(TransactionDTO::fromTransaction).collect(Collectors.toList());
        this.importDate = transactionImport.getImportDate();
        this.importCount = transactionImport.getImportCount();
        final User user = transactionImport.getUser();
        this.user = UserDTO.fromUser(user);
    }

}
