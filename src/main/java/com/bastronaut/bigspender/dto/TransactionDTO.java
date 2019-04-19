package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.enums.TransactionCode;
import com.bastronaut.bigspender.enums.TransactionMutationType;
import com.bastronaut.bigspender.enums.TransactionType;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class TransactionDTO {

    private long id;
    private final LocalDate date;
    private final LocalTime time;
    private final String name;
    private final String accountNumber;
    private final String receivingAccountNumber;
    private final TransactionCode code;
    private final TransactionType type;
    private final long amount;
    private final TransactionMutationType mutationType;
    private final String statement;
    private final DayOfWeek day;

    public TransactionDTO(Transaction transaction) {
        this.date = transaction.getDate();
        this.time = transaction.getTime();
        this.name = transaction.getName();
        this.accountNumber = transaction.getAccountNumber();
        this.receivingAccountNumber = transaction.getReceivingAccountNumber();
        this.code = transaction.getCode();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.mutationType = transaction.getMutationType();
        this.statement = transaction.getStatement();
        this.day = transaction.getDay();
    }
}
