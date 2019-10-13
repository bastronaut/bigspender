package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionImportRepository;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionImportRepository transactionImportRepository;


    public List<Transaction> getTransactionsForUser(final User user) {

        return transactionRepository.findAllByUser(user);
    }

    /**
     *
     * @param user
     * @param transactionId
     * @return
     */
    public Optional<Transaction> getTransactionForUser(final long transactionId, final User user) {
        return transactionRepository.findByIdAndUser(transactionId, user);
    }


    public long deleteTransactionForUser(final long transactionId, final User user) {

        return transactionRepository.deleteByIdAndUser(transactionId, user);
    }

    public long deleteTransactionsForUser(final User user) {

        return transactionRepository.deleteByUser(user);
    }

    /**
     * Deletes the transactions
     * @param transactionIds the transaction ids to delete
     * @param user the user to delete transactions for
     * @return the list of deleted Transactions
     */
    public List<Transaction> deleteTransactionsForUser(final List<Long> transactionIds, final User user) {
        final List<Transaction> transactions = transactionRepository.deleteByIdInAndUser(transactionIds, user);
        return transactions;
    }

    public TransactionImport saveTransactionImport(final TransactionImport transactionImport) {
        return transactionImportRepository.save(transactionImport);
    }


    public Transaction saveTransaction(final Transaction transaction) {

        return transactionRepository.save(transaction);
    }

    public List<Transaction> saveTransactions(Set<Transaction> transactions) {
        List<Transaction> savedTransactions = transactionRepository.saveAll(transactions);
        transactionRepository.flush();
        return savedTransactions;
    }

    public Set<Transaction> getTransactionsByLabelId(final long labelId, final User user) {
        return transactionRepository.findByLabels_idAndUser(labelId, user);
    }
}
