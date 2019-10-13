package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.in.TransactionAddDTO;
import com.bastronaut.bigspender.dto.out.TransactionDTO;
import com.bastronaut.bigspender.dto.in.TransactionDeleteDTO;
import com.bastronaut.bigspender.dto.out.TransactionDeleteResultDTO;
import com.bastronaut.bigspender.dto.out.TransactionsGetByLabelDTO;
import com.bastronaut.bigspender.exceptions.LabelException;
import com.bastronaut.bigspender.exceptions.TransactionException;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_INVALID_TXID;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_MISSING_TRANSACTION_IDS;
import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_NONEXISTINGTX_FOR_USER;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTIONS_BY_LABEL_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTIONS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for performing operations on Transaction resources.
 *
 * This controller is not used for adding transactions through an import functionality by CSV file.
 * This is done in the import controller
 */
@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @GetMapping(value = TRANSACTIONS_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionDTO>> getTransactions(final @AuthenticationPrincipal User user) {

        final List<Transaction> transactions = transactionService.getTransactionsForUser(user);
        final List<TransactionDTO> result = transactions.stream()
                .map(TransactionDTO::fromTransaction)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = TRANSACTION_ENDPOINT, produces =  APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> getTransaction( final @AuthenticationPrincipal User user,
                                                            final @PathVariable long transactionid) {

        final Optional<Transaction> transaction = transactionService.getTransactionForUser(transactionid, user);

        if (transaction.isPresent()) {
            final TransactionDTO transactionDTO = TransactionDTO.fromTransaction(transaction.get());
            return ResponseEntity.status(HttpStatus.OK).body(transactionDTO);
        } else {
            throw new TransactionException(String.format(ERRORMSG_NONEXISTINGTX_FOR_USER,
                    transactionid, String.valueOf(user.getId())));
        }
    }

    @GetMapping(path = TRANSACTIONS_BY_LABEL_ENDPOINT,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionsGetByLabelDTO> getTransactionsByLabel(final @AuthenticationPrincipal User user,
                                                                            final @PathVariable @NotNull long labelid) {

        Set<Transaction> transactionsById = transactionService.getTransactionsByLabelId(labelid, user);
        Set<TransactionDTO> transactionsDTO = TransactionDTO.fromTransactions(transactionsById);
        return ResponseEntity.status(HttpStatus.OK).body(new TransactionsGetByLabelDTO(labelid, transactionsDTO));
    }

    @PostMapping(value = TRANSACTIONS_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDTO> postTransaction(final @AuthenticationPrincipal User user,
                                                          @Valid final TransactionAddDTO transactionAddDTO,
                                                          BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        final Transaction transaction = Transaction.fromTransactionAddDTO(transactionAddDTO, user);

        final Transaction savedTransaction = transactionService.saveTransaction(transaction);
        final TransactionDTO result = TransactionDTO.fromTransaction(savedTransaction);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    // TODO add validation to user and request path
    @DeleteMapping(value = TRANSACTIONS_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionDeleteResultDTO> deleteTransactions(final @AuthenticationPrincipal User user,
                                                                         final TransactionDeleteDTO transactionDeleteDTO) {

        if (transactionDeleteDTO.getTransactionIds() == null || transactionDeleteDTO.getTransactionIds().size() == 0) {
            // We could add a delete all here, but no functionality for it right now
            throw new TransactionException(ERRORMSG_MISSING_TRANSACTION_IDS);
        }
        final List<Transaction> deletedTransactions = transactionService
                .deleteTransactionsForUser(transactionDeleteDTO.getTransactionIds(), user);

        final TransactionDeleteResultDTO deleteDTO = new TransactionDeleteResultDTO(deletedTransactions);

        return ResponseEntity.status(HttpStatus.OK).body(deleteDTO);
    }

    /**
     * REST Delete method should return a 204 for successful deletion but no entity in response,
     * and a 404 when attempting to delete a nonexisting resource. https://restfulapi.net/http-methods/#delete
     * @param user the user to delete for
     * @param transactionid the transaction id to delete
     * @return
     */
    @DeleteMapping(value = TRANSACTION_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity deleteTransaction(final @AuthenticationPrincipal User user, final @PathVariable long transactionid) {

        final long deleted = transactionService.deleteTransactionForUser(transactionid, user);
        if (deleted > 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    private void checkBindingErrors(final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0);
            throw new TransactionException(error.getDefaultMessage());
        }
    }


}
