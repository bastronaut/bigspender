package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
public class TransactionImportResultDTO {

    private final List<TransactionDTO> transactions;
    private final LocalDate importDate;
    private final int importCount;
    private final UserDTO user;


    public static TransactionImportResultDTO fromTransactionImport(TransactionImport transactionImport) {
        final List<Transaction> transactions = transactionImport.getTransactions();
        final List<TransactionDTO> transactionDTOs = transactions.stream().map(TransactionDTO::fromTransaction).collect(Collectors.toList());
        final UserDTO userDTO = UserDTO.fromUser(transactionImport.getUser());
        return new TransactionImportResultDTO(transactionDTOs, transactionImport.getImportDate(),
                transactionImport.getImportCount(), userDTO);
    }

}
