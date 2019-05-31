package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.in.TransactionAddDTO;
import com.bastronaut.bigspender.dto.out.TransactionDTO;
import com.bastronaut.bigspender.dto.in.TransactionDeleteDTO;
import com.bastronaut.bigspender.dto.out.TransactionDeleteResultDTO;
import com.bastronaut.bigspender.exceptions.TransactionException;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTIONS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * TODO has become messy
 */
@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = TRANSACTIONS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDTO>> getTransactions(final @PathVariable String userid,
                                                               final @AuthenticationPrincipal User user) {

        final List<Transaction> transactions = transactionService.getTransactionsForUser(user);
        final List<TransactionDTO> result = transactions.stream().map(TransactionDTO::fromTransaction).collect(Collectors.toList());
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

        final Optional<Transaction> transaction = transactionService.getTransactionForUser(parsedTransactionId, user);

        if (transaction.isPresent()) {
            final TransactionDTO transactionDTO = TransactionDTO.fromTransaction(transaction.get());
            return ResponseEntity.status(HttpStatus.OK).body(transactionDTO);
        } else {
            throw new TransactionException(String.format("Transaction with id %s for user %s does not exist",
                    transactionid, String.valueOf(user.getId())));
        }
    }

    @PostMapping(value = TRANSACTIONS_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> postTransaction(final @AuthenticationPrincipal User user,
                                                          final TransactionAddDTO transactionDTO) {
        final Transaction transaction = Transaction.fromTransactionDTO(transactionDTO, user);
        // TODO validation on transactionDTO, probably on @Valid in method boddy
        final Transaction savedTransaction = transactionService.saveTransaction(transaction);
        final TransactionDTO result = TransactionDTO.fromTransaction(savedTransaction);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    // TODO add validation to user and request path
    @DeleteMapping(value = TRANSACTIONS_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDeleteResultDTO> deleteTransactions(final @AuthenticationPrincipal User user,
                                                                         final TransactionDeleteDTO transactionDeleteDTO) {

        final List<Transaction> deletedTransactions = transactionService.deleteUserTransactions(transactionDeleteDTO.getTransactionIds(), user);

        final TransactionDeleteResultDTO deleteDTO = new TransactionDeleteResultDTO(deletedTransactions);

        return ResponseEntity.status(HttpStatus.OK).body(deleteDTO);
    }

    // TODO add validation to user and request path
    /**
     * REST Delete method should return a 204 for successfull deletion but no entity in response,
     * and a 404 when attempting to delete a nonexisting resource. https://restfulapi.net/http-methods/#delete
     * @param user the user to delete for
     * @param transactionid the transaction id to delete
     * @return
     */
    @DeleteMapping(value = TRANSACTION_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity deleteTransaction(final @AuthenticationPrincipal User user,
                                            final @PathVariable long transactionid) {

        final long deleted = transactionService.deleteUserTransaction(transactionid, user);
        if (deleted > 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
