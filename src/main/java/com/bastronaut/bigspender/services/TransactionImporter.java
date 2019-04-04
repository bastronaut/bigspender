package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Transaction;

import java.io.InputStream;
import java.util.List;

public interface TransactionImporter {

    List<Transaction> parseTransactions(InputStream source);
}
