package com.bastronaut.bigspender.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

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

/**
 * Entity class for transactions. Can not auto-generate constructor because of the @GeneratedValue id, and creating
 * a superclass for the single class seems undesirable.
 */
@Entity
@Data
@Table(name = "transactions")
@EqualsAndHashCode
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Getter
    private final LocalDate date;

    @Getter
    private final LocalTime time;

    @Getter
    private final String name;

    @Getter
    private final String accountNumber;

    @Getter
    private final String receivingAccountNumber;

    @Getter
    private final TransactionCode code;

    @Getter
    private final TransactionType type;

    // Should consider using BigDecimal but poc is small transactions
    @Getter
    private final long amount;

    @Getter
    private final TransactionMutationType mutationType;

    @Getter
    private final String statement;

    // non-normalized, maybe useful for training data
    @Getter
    private final DayOfWeek day;


    public Transaction(final LocalDate date, final LocalTime time, @NonNull final String name,
                       @NonNull final String accountNumber, final String receivingAccountNumber,
                       @NonNull final TransactionCode code, @NonNull final TransactionType type, @NonNull final long amount,
                       @NonNull final TransactionMutationType mutationType, @NonNull final String statement) {
        this.date = date;
        this.day = date.getDayOfWeek();
        this.time = time;
        this.name = name;
        this.accountNumber = accountNumber;
        this.receivingAccountNumber = receivingAccountNumber;
        this.code = code;
        this.type = type;
        this.amount = amount;
        this.mutationType = mutationType;
        this.statement = statement;
    }

}
