package com.bastronaut.bigspender.services;


import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.utils.SampleData;
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
import java.util.List;

import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class INGTransactionParserImplTest {

    @Autowired
    INGTransactionParserImpl importer;

    private List<Transaction> expectedSampleTransactions;

    private FileInputStream input;
    private User sampleUser;

    @Before
    public void setupSampleResult() throws FileNotFoundException {
        expectedSampleTransactions = SampleData.getTransactions();

        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        this.input = new FileInputStream(sampleFile);
        this.sampleUser = SampleData.getTestUser();
    }

    @Test
    public void testParseTransactions() throws IOException {

        final TransactionImport parsedTransactions = importer.parseTransactions(input, sampleUser);
        assertEquals("Seven transactions have been parsed", 7, parsedTransactions.getImportCount());
        List<Transaction> transactions = parsedTransactions.getTransactions();
        for (int i = 0; i < transactions.size(); i++) {
            assertEquals(expectedSampleTransactions.get(i), transactions.get(i));
        }
    }


}
