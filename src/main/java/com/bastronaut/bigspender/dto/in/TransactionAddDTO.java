package com.bastronaut.bigspender.dto.in;

import com.bastronaut.bigspender.enums.TransactionCode;
import com.bastronaut.bigspender.enums.TransactionMutationType;
import com.bastronaut.bigspender.enums.TransactionType;
import com.bastronaut.bigspender.models.Transaction;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Unlike Transaction entity, does not have the fields (as its unlikely to be added by the user):
 * - TransactionCode
 * - TransactionMutationType
 */
@Getter
public class TransactionAddDTO {

    private long id;
    private final String date;
    private final String time;
    private final String name;
    private final String accountNumber;
    private final String receivingAccountNumber;
    private final String type;
    private final long amount;
    private final String statement;

    public TransactionAddDTO(final String date, final String time, final String name,
                             final String accountNumber, final String receivingAccountNumber, final String type,
                             final long amount, final String statement) {
        this.date = date;
        this.time = time;
        this.name = StringUtils.isNotBlank(name) ? name : null;
        this.accountNumber = StringUtils.isNotBlank(accountNumber) ? accountNumber : null;
        this.receivingAccountNumber = StringUtils.isNotBlank(receivingAccountNumber) ? receivingAccountNumber : null;;
        this.type = TransactionType.getByType(type).getType();
        this.amount = amount;
        this.statement = StringUtils.isNotBlank(statement) ? statement : null;
    }


    public static TransactionAddDTO fromTransaction(Transaction transaction) {
        final TransactionAddDTO transactionDTO = new TransactionAddDTO(getDateString(transaction.getDate()),
                getTimeString(transaction.getTime()), transaction.getName(), transaction.getAccountNumber(),
                transaction.getReceivingAccountNumber(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getStatement());

        transaction.setId(transaction.getId());
        return transactionDTO;
    }

    private static String getDateString(LocalDate date) {
        if (date == null) return null;

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(dtf);
    }


    private static String getTimeString(LocalTime time) {
        if (time == null) return null;

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(dtf);
    }

    private static String getMutationType(TransactionMutationType mutationType) {
        if (mutationType == null) return null;

        return mutationType.getType();
    }

}
