package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.Transaction;

import java.time.LocalDate;
import java.util.List;


public class TransactionImportDTO {

    private final List<Transaction> transactions;
    private final LocalDate importDate;
    private final int importCount;

}
