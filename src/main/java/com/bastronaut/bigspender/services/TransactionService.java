package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.exceptions.TransactionException;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionImportRepository;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionImportRepository transactionImportRepository;

    /**
     *
     * @param user The user to get transactions for
     * @return
     */
    public List<Transaction> getTransactionsForUser(final User user) {
        return transactionRepository.findAllByUserId(user.getId());
    }

    /**
     *
     * @param user
     * @param transactionId
     * @return
     */
    public Optional<Transaction> getTransactionForUser(final User user, final long transactionId) {
//        return transactionRepository.findbyUserIdAndId(user.getId(), transactionId);
        return null;
    }

    /**
     *
     * @param user
     * @param transactionId
     * @return
     */
    public long deleteTransactionForUser(final User user, final long transactionId) {
//        final Optional<Transaction> transaction = transactionRepository.findbyUserIdAndId(user.getId(),
//                transactionId);
//        if (!transaction.isPresent()) {
//            throw new TransactionException(String.format("Transaction with id %s not found for user %s",
//                    transactionId, user.getId()));
//        }
//        return transactionRepository.remove(transaction.get());
        return 1;
        // TODO
    }

    public TransactionImport saveTransactionImport(final TransactionImport transactionImport) {
        return transactionImportRepository.save(transactionImport);
    }


    /**
     *
     * @param user
     * @return
     */
    public long deleteInBulkByUserId(final User user) {
        final long nrOfTransactions = transactionRepository.countByUserId(user.getId());
        if (nrOfTransactions == 0) {
            throw new TransactionException("No transactions for user found");
        }
        return transactionRepository.deleteInBulkByUserId(user.getId());
    }

    public Transaction saveTransaction(final Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
