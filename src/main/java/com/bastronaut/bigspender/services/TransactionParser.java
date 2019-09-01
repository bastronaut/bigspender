package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Transaction;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

public interface TransactionParser {

    List<Transaction> parseTransactions(final InputStream source);
}
