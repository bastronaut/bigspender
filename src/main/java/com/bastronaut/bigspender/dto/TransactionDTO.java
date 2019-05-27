package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.enums.TransactionCode;
import com.bastronaut.bigspender.enums.TransactionMutationType;
import com.bastronaut.bigspender.enums.TransactionType;
import com.bastronaut.bigspender.models.Transaction;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

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

    public TransactionDTO(final String date, final String time, final String name,
                          final String accountNumber, final String receivingAccountNumber, final String code,
                          final String type, final String amount, final String mutationType, final String statement) {
        this.date = determineDate(date);
        this.time = determineTime(time);
        this.name = StringUtils.isNotBlank(name) ? name : null;
        this.accountNumber = StringUtils.isNotBlank(accountNumber) ? accountNumber : null;
        this.receivingAccountNumber = StringUtils.isNotBlank(receivingAccountNumber) ? receivingAccountNumber : null;;
        this.code = TransactionCode.getByCode(code);
        this.type = TransactionType.getByType(type);
        this.amount = determineAmount(amount);
        this.mutationType = determineMutationType(mutationType);
        this.statement = StringUtils.isNotBlank(statement) ? statement : null;
        this.day = this.date != null ? this.date.getDayOfWeek() : null;
    }

    private void setId(final long id) {
        this.id = id;
    }

    private LocalDate determineDate(final String date) {
        if (date == null) return null;

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(date, dtf);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalTime determineTime(final String time) {
        if (time == null) return null;

        try {
            return LocalTime.parse(time);
        } catch(DateTimeParseException e) {
            return null;
        }
    }

    private long determineAmount(final String amount) {
        try {
            Number parsed = NumberFormat.getInstance().parse(amount);
            return parsed.longValue();
        } catch (ParseException e) {
            return 0;
        }
    }

    private TransactionMutationType determineMutationType(final String mutationType) {
        if (StringUtils.isNotBlank(mutationType)) {
            return TransactionMutationType.getByValue(mutationType);
        } else {
            return null;
        }
    }


    public static TransactionDTO fromTransaction(Transaction transaction) {
        final TransactionDTO transactionDTO = new TransactionDTO(getDateString(transaction.getDate()),
                getTimeString(transaction.getTime()), transaction.getName(), transaction.getAccountNumber(),
                transaction.getReceivingAccountNumber(), transaction.getCode().toString(),
                transaction.getType().toString(), Long.toString(transaction.getAmount()),
                getMutationType(transaction.getMutationType()), transaction.getStatement());

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
