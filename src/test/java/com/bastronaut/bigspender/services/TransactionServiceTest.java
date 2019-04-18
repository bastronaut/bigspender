package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.bastronaut.bigspender.utils.SampleTransactions.t1;
import static com.bastronaut.bigspender.utils.SampleTransactions.t2;
import static com.bastronaut.bigspender.utils.SampleTransactions.t3;
import static com.bastronaut.bigspender.utils.SampleTransactions.t4;
import static com.bastronaut.bigspender.utils.SampleTransactions.t5;
import static com.bastronaut.bigspender.utils.SampleTransactions.t6;
import static com.bastronaut.bigspender.utils.SampleTransactions.t7;

public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Before
    public void init() {
        // Insert sample transactions into DB
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);
        transactions.add(t3);
        transactions.add(t4);
        transactions.add(t5);
        transactions.add(t6);
        transactions.add(t7);
        transactionRepository.save(transactions);
    }

    @Test
    public void testGetTransactionsForUser() {
        assert(false);
    }

    @Test
    public void testGetTransactionForUser() {
        assert(false);
    }

    @Test
    public void testDeleteTransactionForUser() {
        assert(false);
    }

    @Test
    public void testDeleteTransactionsForUser() {
        assert(false);
    }

    @Test
    public void testInsertTransactionForUser() {
        assert(false);
    }

    @Test
    public void testInsertTransactionsForUser() {
        assert(false);
    }

    @Test
    public void testInsertInvalidTransactions() {
        assert(false);
    }

    @Test
    public void testInsertTransactionNonexistingUser() {
        assert(false);
    }

}