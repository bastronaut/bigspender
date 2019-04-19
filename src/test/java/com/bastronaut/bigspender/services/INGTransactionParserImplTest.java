package com.bastronaut.bigspender.services;


import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.utils.SampleTransactions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.bastronaut.bigspender.enums.TransactionCode.BA;
import static com.bastronaut.bigspender.enums.TransactionCode.GT;
import static com.bastronaut.bigspender.enums.TransactionMutationType.BETAALAUTOMAAT;
import static com.bastronaut.bigspender.enums.TransactionMutationType.DIVERSEN;
import static com.bastronaut.bigspender.enums.TransactionMutationType.ONLINEBANKIEREN;
import static com.bastronaut.bigspender.enums.TransactionType.AF;
import static com.bastronaut.bigspender.enums.TransactionType.BIJ;
import static com.bastronaut.bigspender.utils.SampleTransactions.t1;
import static com.bastronaut.bigspender.utils.SampleTransactions.t2;
import static com.bastronaut.bigspender.utils.SampleTransactions.t3;
import static com.bastronaut.bigspender.utils.SampleTransactions.t4;
import static com.bastronaut.bigspender.utils.SampleTransactions.t5;
import static com.bastronaut.bigspender.utils.SampleTransactions.t6;
import static com.bastronaut.bigspender.utils.SampleTransactions.t7;
import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class INGTransactionParserImplTest {

    @Autowired
    INGTransactionParserImpl importer;

    private List<Transaction> expectedSampleTransactions;

    private FileInputStream input;

    @Before
    public void setupSampleResult() throws FileNotFoundException {
        expectedSampleTransactions = SampleTransactions.getSampleTransactions();
    }

    @Test
    public void testParseTransactions() throws IOException {

        final TransactionImport parsedTransactions = importer.parseTransactions(input, new User("as", "asd", "asd"));
        assertEquals("Seven transactions have been parsed", 7, parsedTransactions.getImportCount());
        List<Transaction> transactions = parsedTransactions.getTransactions();
        for (int i = 0; i < transactions.size(); i++) {
            assertEquals(expectedSampleTransactions.get(i), transactions.get(i));
        }
    }


}
