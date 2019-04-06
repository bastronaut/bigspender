package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionCode;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.TransactionMutationType;
import com.bastronaut.bigspender.models.TransactionType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_ACCOUNT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_AMOUNT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_CODE;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_DATE;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_MUTATIONTYPE;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_NAME;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_RECEIVINGACCOUNT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_STATEMENT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.CSV_HEADER_TYPE;
import static com.bastronaut.bigspender.utils.ApplicationConstants.EXPECTED_NR_COLUMNS_ING;
import static com.bastronaut.bigspender.utils.ApplicationConstants.HH_MM_SS_TIMEPATTERN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.HH_MM_TIMEPATTERN;

@Service
public class INGTransactionImporterImpl {

    private final Logger logger = LoggerFactory.getLogger(INGTransactionImporterImpl.class);

    /**
     * Parses the transactions represented in the format exported by the ING CSV transaction downloader. Assumes
     * the first line is the header line and skips it (as is the case for all ING exports).
     *
     * @param source an InputStream of the transactions CSV file
     * @return a List of Transactions. If the transaction is invalid, the transaction is skipped entirely. A
     * transaction is invalid if it does not have the expected number of columns
     */
    public TransactionImport parseTransactions(final InputStream source) throws IOException {

        final Reader reader = new InputStreamReader(source);
        final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

        List<Transaction> transactions = StreamSupport.stream(parser.spliterator(), false)
                .map(this::parseTransaction)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new TransactionImport(transactions);
    }

    @Nullable
    private Transaction parseTransaction(CSVRecord transaction) {
        if (!isValidTransaction(transaction)) {
            return null;
        }

        final LocalDate date = determineDate(transaction);
        final LocalTime time = determineTime(transaction);
        final String name = determineName(transaction);
        final String accountNr = determineAccountNumber(transaction);
        final String receivingAccountNr = determineReceivingAccountNumber(transaction);
        final TransactionCode code = determineTransactionCode(transaction);
        final TransactionType type = determineTransactionType(transaction);
        final long amount = determineAmount(transaction);
        final TransactionMutationType mutationType = determineTransactionMutationType(transaction);
        final String statement = determineStatement(transaction);
        return new Transaction(date, time, name, accountNr, receivingAccountNr, code, type, amount, mutationType, statement);
    }


    private boolean isValidTransaction(final CSVRecord transaction) {
        return transaction.size() == EXPECTED_NR_COLUMNS_ING;
    }

    private LocalDate determineDate(final CSVRecord transaction) {
        final String date = transaction.get(CSV_HEADER_DATE);

        if (StringUtils.length(date) != 8) { return null; }

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            return LocalDate.parse(date, dtf);
        } catch (DateTimeParseException e) {
            logger.info(String.format("Can't determine date for: %s", date), e);
            return null;
        }
    }

    /**
     * Attempts to find a time in a free form 'statement' column of a transaction export. Can be of either form:
     * hh:mm:ss or hh:mm
     * @param transaction the string containing the transaction Statement
     * @return a LocalTime if found, or null if not found
     */
    private LocalTime determineTime(final CSVRecord transaction) {
        final String statement = transaction.get(CSV_HEADER_STATEMENT);

        String timePaid = getMatchingRegex(statement, HH_MM_SS_TIMEPATTERN);
        if (StringUtils.isBlank(timePaid)) {
            timePaid = getMatchingRegex(statement, HH_MM_TIMEPATTERN);
        }
        if (StringUtils.isBlank(timePaid)) return null;

        // Regexes may contain spaces to ensure accurate match with time pattern, strip them
        timePaid = timePaid.replaceAll(" ", "");

        try {
            return LocalTime.parse(timePaid);
        } catch (DateTimeParseException e) {
            logger.info(String.format("Can't determine time for: %s", statement), e);
            return null;
        }
    }


    private String getMatchingRegex(String charSequence, String regexPattern) {
        final Pattern pattern = Pattern.compile(regexPattern);
        final Matcher matcher = pattern.matcher(charSequence);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return StringUtils.EMPTY;
    }

    private String determineName(final CSVRecord transaction) {
        return transaction.get(CSV_HEADER_NAME);
    }

    private String determineAccountNumber(final CSVRecord transaction) {
        return transaction.get(CSV_HEADER_ACCOUNT);
    }

    @Nullable
    private String determineReceivingAccountNumber(final CSVRecord transaction) {
        final String receivingAcct = transaction.get(CSV_HEADER_RECEIVINGACCOUNT);
        return StringUtils.isNotBlank(receivingAcct) ? receivingAcct : null;
    }

    private TransactionCode determineTransactionCode(final CSVRecord transaction) {
        return TransactionCode.getByCode(transaction.get(CSV_HEADER_CODE));

    }

    private TransactionType determineTransactionType(final CSVRecord transaction) {
        return TransactionType.getByType(transaction.get(CSV_HEADER_TYPE));
    }


    /**
     * Parses euro amount of String in format xx.xx to long of format xxxx
     * @param transaction monetary amount with a period for decimals, such as "123.45"
     * @return The monetary amount in cents as long, (l12345)
     */
    private long determineAmount(final CSVRecord transaction) {
        final String amount = transaction.get(CSV_HEADER_AMOUNT);
        final String amountCents = amount.replace(".", "").replace(",","");
        try {
            Number parsed = NumberFormat.getInstance().parse(amountCents);
            return parsed.longValue();
        } catch (ParseException e) {
            logger.info(String.format("Can't parse number to long: %s", amount), amount);
        }
        return (long) 0;
    }

    private TransactionMutationType determineTransactionMutationType(final CSVRecord transaction) {
        return TransactionMutationType.getByValue(transaction.get(CSV_HEADER_MUTATIONTYPE));
    }

    private String determineStatement(final CSVRecord transaction) {
        return transaction.get(CSV_HEADER_STATEMENT);
    }
}
