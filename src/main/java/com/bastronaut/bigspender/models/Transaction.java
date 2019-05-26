package com.bastronaut.bigspender.models;


import com.bastronaut.bigspender.dto.TransactionDTO;
import com.bastronaut.bigspender.enums.TransactionCode;
import com.bastronaut.bigspender.enums.TransactionMutationType;
import com.bastronaut.bigspender.enums.TransactionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity class for transactions. Can not auto-generate constructor because of the @GeneratedValue id, and creating
 * a superclass for the single class seems undesirable.
 */
@Entity
@Data
@Table(name =  "transactions")
@EqualsAndHashCode
public class Transaction {

    @Getter
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id")
    private long id;

    @Getter
    @Column(nullable = false)
    private final LocalDate date;

    @Getter
    @Column(nullable = true)
    private final LocalTime time;

    @Getter
    @Column(nullable = false)
    private final String name;

    @Getter
    @Column(nullable = false, name="account_number")
    private final String accountNumber;

    @Getter
    @Column(nullable = true, name = "receiving_account_number")
    private final String receivingAccountNumber;

    @Getter
    @Column(nullable = true)
    private final TransactionCode code;

    @Getter
    @Column(nullable = false)
    private final TransactionType type;

    // Should consider using BigDecimal but poc is small transactions
    @Getter
    @Column(nullable = false)
    private final long amount;

    @Getter
    @Column(nullable = true, name = "mutation_type")
    private final TransactionMutationType mutationType;

    @Getter
    @Column(nullable = true, length = 512)
    private final String statement;

    @Getter
    @Column(nullable = false)
    private final DayOfWeek day; // non-normalized, maybe useful for training data

    @Getter
    @JoinColumn(name="user_id", nullable = false)
    @ManyToOne
    private final User user;

    public Transaction(final LocalDate date, final LocalTime time, @NonNull final String name,
                       @NonNull final String accountNumber, final String receivingAccountNumber,
                       final TransactionCode code, @NonNull final TransactionType type, final long amount,
                       @NonNull final TransactionMutationType mutationType, @NonNull final String statement,
                       final User user) {
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
        this.user = user;
    }

    public static Transaction fromTransactionDTO(final TransactionDTO transactionDTO, final User user) {
        return new Transaction(transactionDTO.getDate(), transactionDTO.getTime(), transactionDTO.getName(),
                transactionDTO.getAccountNumber(), transactionDTO.getReceivingAccountNumber(), transactionDTO.getCode(),
                transactionDTO.getType(), transactionDTO.getAmount(), transactionDTO.getMutationType(),
                transactionDTO.getStatement(), user);
    }

}
