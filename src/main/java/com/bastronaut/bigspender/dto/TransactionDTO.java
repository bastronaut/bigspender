package com.bastronaut.bigspender.dto;

import com.bastronaut.bigspender.enums.TransactionCode;
import com.bastronaut.bigspender.enums.TransactionMutationType;
import com.bastronaut.bigspender.enums.TransactionType;
import com.bastronaut.bigspender.models.Transaction;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
public class TransactionDTO {

    private final Logger logger = LoggerFactory.getLogger(TransactionDTO.class);

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

    public TransactionDTO(final long id, final String date, final String time, final String name,
                          final String accountNumber, final String receivingAccountNumber, final String code,
                          final String type, final long amount, final String mutationType, final String statement) {
        this.id = id;
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

    private LocalDate determineDate(final String date) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd");
        try {
            return LocalDate.parse(date, dtf);
        } catch (DateTimeParseException e) {
            logger.info(String.format("Can't determine date for: %s", date), e);
            return null;
        }
    }

    private LocalTime determineTime(final String time) {
        try {
            return LocalTime.parse(time);
        } catch(DateTimeParseException e) {
            logger.info(String.format("Can't determine time for: %s", time), e);
            return null;
        }
    }

    private long determineAmount(final String amount) {
        try {
            Number parsed = NumberFormat.getInstance().parse(amount);
            return parsed.longValue();
        } catch (ParseException e) {
            logger.info(String.format("Can't parse number to long: %s", amount), amount);
            return 0;
        }
    }

    private TransactionMutationType determineMutationType(final String mutationType) {
        if (StringUtils.isNotBlank(mutationType)) {
            return TransactionMutationType.valueOf(mutationType);
        } else {
            return null;
        }
    }


    public static TransactionDTO fromTransaction(Transaction transaction) {
        return new TransactionDTO(transaction.getId(), transaction.getDate().toString(),
                transaction.getTime().toString(), transaction.getName(), transaction.getAccountNumber(),
                transaction.getReceivingAccountNumber(), transaction.getCode().toString(),
                transaction.getType().toString(), transaction.getAmount(),
                transaction.getMutationType().toString(), transaction.getStatement());
    }
}
