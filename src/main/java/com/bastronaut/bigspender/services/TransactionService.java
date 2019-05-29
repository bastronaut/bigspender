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


    public long deleteUserTransaction(final long transactionId, final User user) {
        return transactionRepository.deleteByIdAndUser(transactionId, user);
    }

    public long deleteUserTransactions(final User user) {
        return transactionRepository.deleteByUser(user);
    }

    public TransactionImport saveTransactionImport(final TransactionImport transactionImport) {

        return transactionImportRepository.save(transactionImport);
    }


    public Transaction saveTransaction(final Transaction transaction) {

        return transactionRepository.save(transaction);
    }
}
