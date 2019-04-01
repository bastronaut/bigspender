package com.bastronaut.bigspender.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;


@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Date date;

    private String name;

    private String accountNumber;

    private String receivingAccountNumber;

    private String code;

    // todo enum af / bij
    private String type;

    private long amount;

    // todo enum
    private String mutationType;

    // mededeling
    private String statement;

    private Time time;

    // non-normalized, maybe useful for training data
    private DayOfWeek day;


}
