package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.TransactionDTO;
import com.bastronaut.bigspender.exceptions.TransactionException;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bastronaut.bigspender.enums.TransactionCode.BA;
import static com.bastronaut.bigspender.enums.TransactionCode.GT;
import static com.bastronaut.bigspender.enums.TransactionMutationType.BETAALAUTOMAAT;
import static com.bastronaut.bigspender.enums.TransactionMutationType.ONLINEBANKIEREN;
import static com.bastronaut.bigspender.enums.TransactionType.AF;
import static com.bastronaut.bigspender.enums.TransactionType.BIJ;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTIONS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = TRANSACTIONS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDTO>> getTransactions(final @PathVariable String userid,
                                                               final @AuthenticationPrincipal User user) {

        final List<Transaction> transactions = transactionService.getTransactionsForUser(user);
        final List<TransactionDTO> result = transactions.stream().map(TransactionDTO::new).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = TRANSACTION_ENDPOINT, produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> getTransaction( final @AuthenticationPrincipal User user,
                                                            final @PathVariable String userid,
                                                            final @PathVariable String transactionid) {
        final long parsedTransactionId;
        try {
            parsedTransactionId = Long.parseLong(transactionid);
        } catch (NumberFormatException e) {
            throw new TransactionException(String.format("Invalid transaction id: %s", transactionid));
        }

        final Optional<Transaction> transaction = transactionService.getTransactionForUser(user, parsedTransactionId);

        if (transaction.isPresent()) {
            final TransactionDTO transactionDTO = new TransactionDTO(transaction.get());
            return ResponseEntity.status(HttpStatus.OK).body(transactionDTO);
        } else {
            throw new TransactionException(String.format("Transaction with id %s for user %s does not exist",
                    transactionid, String.valueOf(user.getId())));
        }
    }

    @PostMapping(value = TRANSACTIONS_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> postTransaction(final @AuthenticationPrincipal User user,
                                                          final TransactionDTO transactionDTO) {
        final Transaction transaction = Transaction.fromTransactionDTO(transactionDTO, user);
        // TODO validation on transactionDTO, probably on @Valid in method boddy
        final Transaction savedTransaction = transactionService.saveTransaction(transaction);
        final TransactionDTO result = new TransactionDTO(savedTransaction);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
