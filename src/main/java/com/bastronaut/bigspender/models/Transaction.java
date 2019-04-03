package com.bastronaut.bigspender.models;


import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDate date;

    private LocalTime time;

    private String name;

    private String accountNumber;

    private String receivingAccountNumber;

    private String code;

    private TransactionType type;

    private long amount;

    private TransactionMutationType mutationType;

    // mededeling
    private String statement;

    // non-normalized, maybe useful for training data
    private DayOfWeek day;


}
