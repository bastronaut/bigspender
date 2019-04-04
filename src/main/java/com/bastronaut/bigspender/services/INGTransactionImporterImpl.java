package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionCode;
import com.bastronaut.bigspender.models.TransactionMutationType;
import com.bastronaut.bigspender.models.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ACCT_COLUMN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.AMOUNT_COLUMN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.EXPECTED_NR_COLUMNS_ING;
import static com.bastronaut.bigspender.utils.ApplicationConstants.MUTATIONCODE_COLUMN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.MUTATIONTYPE_COLUMN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.NAME_COLUMN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.RECEIVINGACCTNR_COLUMN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.STATEMENT_COLUMN;
import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTIONTYPE_COLUMN;
import static java.util.stream.Collectors.toList;

@Service
public class INGTransactionImporterImpl {

    final Logger logger = LoggerFactory.getLogger(INGTransactionImporterImpl.class);


    /**
     * Parses the transactions represented in the format exported by the ING CSV transaction downloader. Assumes
     * the first line is the header line and skips it (as is the case for all ING exports).
     *
     * @param source an InputStream of the transactions CSV file
     * @return a List of Transactions. If the transaction is invalid, the transaction is skipped entirely. A
     * transaction is invalid if it does not have the expected number of columns
     */
    public List<Transaction> parseTransactions(InputStream source) {
        List<Transaction> transactions;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(source))) {
            transactions = reader.lines().skip(1).map(this::parseTransaction).collect(toList());
    } catch (IOException e) {
            logger.debug("Error reading transactions", e);
        }
        return new ArrayList<>();
    }

    private Transaction parseTransaction(String transaction) {
        // Splits CSV on commas outside of string fields, means it will work if commas are present in texts
        // See: https://stackoverflow.com/questions/18893390/splitting-on-comma-outside-quotes
        String[] columns = transaction.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        return new Transaction(LocalDate.of(2019, 01, 01), null, null, null, null, null,
                null, 0, TransactionMutationType.BETAALAUTOMAAT, null, DayOfWeek.MONDAY);
    }


    private boolean isValidTransactionLine(String[] columns) {
        return columns.length == EXPECTED_NR_COLUMNS_ING;
    }

    private LocalDate determineDate(final String[] transactionColumns) {
        return LocalDate.of(2000, 00, 00);
    }

    private LocalTime determineTime(final String[] transactionColumns) {
        return LocalTime.of(00, 00, 00);
    }

    private String determineName(final String[] transactionColumns) {
        return transactionColumns[NAME_COLUMN];
    }

    private String determineAccountNumber(final String[] transactionColumns) {
        return transactionColumns[ACCT_COLUMN];
    }

    private String determineReceivingAccountNumber(final String[] transactionColumns) {
        return transactionColumns[RECEIVINGACCTNR_COLUMN];
    }

    private TransactionCode determineTransactionCode(final String[] transactionColumns) {
        return TransactionCode.getByCode(transactionColumns[MUTATIONCODE_COLUMN]);

    }

    private TransactionType determineTransactionType(final String[] transactionColumns) {
        return TransactionType.getByType(transactionColumns[TRANSACTIONTYPE_COLUMN]);
    }

    // parses amount in String with comma to long in cents
    private long determineAmount(final String[] transactionColumns) {
        final String amount = transactionColumns[AMOUNT_COLUMN];
        long parsedAmount;
        try {
            Number parsed = NumberFormat.getInstance().parse(amount);
            
        } catch (ParseException e) {
            logger.info("Can't parse number to long", amount);
            parsedAmount = 0;
        }
        return parsedAmount;
    }

    private TransactionMutationType determineTransactionMutationType(final String[] transactionColumns) {
        return TransactionMutationType.getByValue(transactionColumns[MUTATIONTYPE_COLUMN]);
    }

    private String determineStatement(final String[] transactionColumns) {
        return transactionColumns[STATEMENT_COLUMN];
    }
}
