package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.models.TransactionCode;
import com.bastronaut.bigspender.models.TransactionMutationType;
import com.bastronaut.bigspender.models.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
public class TransactionDTO {

    @Setter
    private final LocalDate date;

    @Setter
    private final LocalTime time;

    @Setter
    private final String name;

    @Setter
    private final String accountNumber;

    @Setter
    private final String receivingAccountNumber;

    @Setter
    private final TransactionCode code;

    @Setter
    private final TransactionType type;

    @Setter
    private final long amount;

    @Setter
    private final TransactionMutationType mutationType;

    @Setter
    private final String statement;

    @Setter
    private final DayOfWeek day;
}
