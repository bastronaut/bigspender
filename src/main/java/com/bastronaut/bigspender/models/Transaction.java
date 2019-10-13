package com.bastronaut.bigspender.models;


import com.bastronaut.bigspender.dto.in.TransactionAddDTO;
import com.bastronaut.bigspender.dto.out.TransactionDTO;
import com.bastronaut.bigspender.enums.TransactionCode;
import com.bastronaut.bigspender.enums.TransactionMutationType;
import com.bastronaut.bigspender.enums.TransactionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Entity class for transactions. Can not auto-generate constructor because of the @GeneratedValue id, and creating
 * a superclass for the single class seems undesirable.
 */
@Entity
//@Data
@Table(name = "transactions")
@NoArgsConstructor
public class Transaction {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id")
    private long id;

    @Setter
    @Getter
    @Column
    private LocalDate date;

    @Setter
    @Getter
    @Column
    private LocalTime time;

    @Setter
    @Getter
    @Column(nullable = false)
    private String name;

    @Setter
    @Getter
    @Column(nullable = false, name = "account_number")
    private String accountNumber;

    @Setter
    @Getter
    @Column(name = "receiving_account_number")
    private String receivingAccountNumber;

    @Setter
    @Getter
    @Column
    private TransactionCode code;

    @Setter
    @Getter
    @Column(nullable = false)
    private TransactionType type;

    // Should consider using BigDecimal but poc is small transactions
    @Setter
    @Getter
    @Column(nullable = false)
    private long amount;

    @Setter
    @Getter
    @Column(name = "mutation_type")
    private TransactionMutationType mutationType;

    @Setter
    @Getter
    @Column(length = 512)
    private String statement;


    @Setter
    @Getter
    @Column(nullable = true)
    private DayOfWeek day; // non-normalized, maybe useful for training data

    @Setter
    @Getter
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;


    public void setLabels(Set<Label> labels) {
        this.labels = new HashSet<>(labels);
    }

    @Getter
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "transaction_label")
    private Set<Label> labels = new HashSet<>();


    public void addLabel(final Label label) {
        this.labels.add(label);
        label.getTransactions().add(this);
    }

    public void removeLabel(final Label label) {
        this.labels.remove(label);
        label.getTransactions().remove(this);
    }

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

        final TransactionMutationType transactionMutationType = TransactionMutationType.UNKNOWN;
        final TransactionCode transactionCode = TransactionCode.UNKNOWN;
        final TransactionType transactionType = TransactionType.getByType(transactionAddDTO.getType());
        final LocalDate transactionDate = determineDate(transactionAddDTO.getDate());
        final LocalTime transactionTime = determineTime(transactionAddDTO.getTime());

        return new Transaction(transactionDate, transactionTime, transactionAddDTO.getName(),
                transactionAddDTO.getAccountNumber(), transactionAddDTO.getReceivingAccountNumber(), transactionCode,
                transactionType, transactionAddDTO.getAmount(), transactionMutationType,
                transactionAddDTO.getStatement(), user);
    }


    private static LocalDate determineDate(final String date) {
        if (date == null) return null;

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(date, dtf);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalTime determineTime(final String time) {
        if (time == null) return null;

        try {
            return LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            return null;
        }
    }


    @Override
    public int hashCode() {
        long res = id + amount + (long) mutationType.hashCode();
        return Objects.hash(res);
    }

    /**
     * Separate equals() to avoid comparing the labels that are attached and going in recursive loop that tests
     * if Transaction is attached to label
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Transaction)) return false;
        final Transaction t = (Transaction) o;
        return  Objects.equals(t.getId(), this.getId()) &&
                Objects.equals(t.getDate(), this.getDate()) &&
                Objects.equals(t.getTime(), this.getTime()) &&
                Objects.equals(t.getName(), this.getName()) &&
                Objects.equals(t.getAccountNumber(), this.getAccountNumber()) &&
                Objects.equals(t.getReceivingAccountNumber(), this.getReceivingAccountNumber()) &&
                Objects.equals(t.getReceivingAccountNumber(), this.getReceivingAccountNumber()) &&
                Objects.equals(t.getAmount(), this.getAmount()) &&
                Objects.equals(t.getMutationType(), this.getMutationType()) &&
                Objects.equals(t.getStatement(), this.getStatement()) &&
                Objects.equals(t.getStatement(), this.getStatement()) &&
                Objects.equals(t.getUser(), this.getUser());

    }


}
