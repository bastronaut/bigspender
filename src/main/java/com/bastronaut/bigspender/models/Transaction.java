package com.bastronaut.bigspender.models;


import com.bastronaut.bigspender.dto.in.TransactionAddDTO;
import com.bastronaut.bigspender.dto.out.TransactionDTO;
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
    @Column(nullable = true)
    private LocalDate date;

    @Getter
    @Column(nullable = true)
    private LocalTime time;

    @Getter
    @Column(nullable = false)
    private String name;

    @Getter
    @Column(nullable = false, name="account_number")
    private String accountNumber;

    @Getter
    @Column(nullable = true, name = "receiving_account_number")
    private String receivingAccountNumber;

    @Getter
    @Column(nullable = true)
    private TransactionCode code;

    @Getter
    @Column(nullable = false)
    private TransactionType type;

    // Should consider using BigDecimal but poc is small transactions
    @Getter
    @Column(nullable = false)
    private long amount;

    @Getter
    @Column(nullable = true, name = "mutation_type")
    private TransactionMutationType mutationType;

    @Getter
    @Column(nullable = true, length = 512)
    private String statement;

    @Getter
    @Column(nullable = true)
    private DayOfWeek day; // non-normalized, maybe useful for training data

    @Getter
    @JoinColumn(name="user_id", nullable = false)
    @ManyToOne
    private User user;

    public Transaction(final LocalDate date, final LocalTime time, @NonNull final String name,
                       @NonNull final String accountNumber, final String receivingAccountNumber,
                       final TransactionCode code, @NonNull final TransactionType type, final long amount,
                       @NonNull final TransactionMutationType mutationType, @NonNull final String statement,
                       final User user) {
        this.date = date;
        if (date != null) {
            this.day = date.getDayOfWeek();
        } else {
            this.day = null;
        }
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

    // default constructor for hibernate
    private Transaction() {}

    public static Transaction fromTransactionDTO(final TransactionDTO transactionDTO, final User user) {
            final TransactionCode txCode = TransactionCode.getByValue(transactionDTO.getCode());
            final TransactionMutationType txMutationType = TransactionMutationType.getByValue(transactionDTO.getType());
            final TransactionType txType = TransactionType.getByType(transactionDTO.getType());

            return new Transaction(transactionDTO.getDate(), transactionDTO.getTime(), transactionDTO.getName(),
                    transactionDTO.getAccountNumber(), transactionDTO.getReceivingAccountNumber(), txCode,
                    txType, transactionDTO.getAmount(), txMutationType,
                    transactionDTO.getStatement(), user);
        }

    public static Transaction fromTransactionAddDTO(final TransactionAddDTO transactionAddDTO, final User user) {

        final TransactionMutationType txMutationType = TransactionMutationType.getByValue(transactionAddDTO.getMutationType());

        final TransactionCode txCode = TransactionCode.getByTransactionCode(transactionAddDTO.getCode());
        final TransactionType txType = TransactionType.getByType(transactionAddDTO.getType());

        return new Transaction(transactionAddDTO.getDate(), transactionAddDTO.getTime(), transactionAddDTO.getName(),
                transactionAddDTO.getAccountNumber(), transactionAddDTO.getReceivingAccountNumber(), txCode,
                txType, transactionAddDTO.getAmount(), txMutationType,
                transactionAddDTO.getStatement(), user);
    }



}
