package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import com.bastronaut.bigspender.enums.TransactionCode;
import com.bastronaut.bigspender.enums.TransactionMutationType;
import com.bastronaut.bigspender.enums.TransactionType;
import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import jdk.nashorn.internal.ir.Labels;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class TransactionDTO {

    private long id;
    private final LocalDate date;
    private final LocalTime time;
    private final String name;
    private final String accountNumber;
    private final String receivingAccountNumber;
    private final String code;
    private final String type;
    private final long amount;
    private final String mutationType;
    private final String statement;
    private final Set<LabelDTO> labels;

    public TransactionDTO(final long id, final String date, final String time, final String name,
                          final String accountNumber, final String receivingAccountNumber, final TransactionCode code,
                          final String type, final String amount, final TransactionMutationType mutationType,
                          final String statement, final Set<Label> labels) {
        this.id = id;
        this.date = determineDate(date);
        this.time = determineTime(time);
        this.name = StringUtils.isNotBlank(name) ? name : null;
        this.accountNumber = StringUtils.isNotBlank(accountNumber) ? accountNumber : null;
        this.receivingAccountNumber = StringUtils.isNotBlank(receivingAccountNumber) ? receivingAccountNumber : null;;
        this.code = getMutationCode(code);
        this.type = TransactionType.getByType(type).getType();
        this.amount = determineAmount(amount);
        this.mutationType = getMutationType(mutationType);
        this.statement = StringUtils.isNotBlank(statement) ? statement : null;
        this.labels = LabelDTO.fromLabels(labels);
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

    public static TransactionDTO fromTransaction(Transaction transaction) {
        final TransactionDTO transactionDTO = new TransactionDTO(transaction.getId(), getDateString(transaction.getDate()),
                getTimeString(transaction.getTime()), transaction.getName(), transaction.getAccountNumber(),
                transaction.getReceivingAccountNumber(), transaction.getCode(),
                transaction.getType().toString(), Long.toString(transaction.getAmount()),
                transaction.getMutationType(), transaction.getStatement(), transaction.getLabels());

        transaction.setId(transaction.getId());
        return transactionDTO;
    }

    public static List<TransactionDTO> fromTransactions(final List<Transaction> transactions) {
        return transactions.stream().map(TransactionDTO::fromTransaction).collect(Collectors.toList());
    }

    public static Set<TransactionDTO> fromTransactions(final Set<Transaction> transactions) {
        return transactions.stream().map(TransactionDTO::fromTransaction).collect(Collectors.toSet());
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

    private static String getMutationType(final TransactionMutationType mutationType) {
        if (mutationType == null) return StringUtils.EMPTY;

        return mutationType.getType();
    }

    private static String getMutationCode(final TransactionCode mutationCode) {
        if (mutationCode == null) return StringUtils.EMPTY;

        return mutationCode.getType();
    }
}
