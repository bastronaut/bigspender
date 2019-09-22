package com.bastronaut.bigspender.services;


import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;

import com.bastronaut.bigspender.utils.SampleData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // required to reset state after test
@SpringBootTest
public class INGTransactionParserImplTest {

    @Autowired
    INGTransactionParserImpl importer;

    private final List<Transaction> expectedSampleTransactions  = SampleData.getTransactions();

    private FileInputStream input;
    private final User testUser = SampleData.TESTUSERONE;

    @Before
    public void setupSampleResult() throws FileNotFoundException {

        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        this.input = new FileInputStream(sampleFile);
    }

    @Test
    public void testParseTransactions() throws IOException {

        final TransactionImport parsedTransactions;
        parsedTransactions = importer.parseTransactions(input, testUser);
        assertEquals("Seven transactions have been parsed", 7, parsedTransactions.getImportCount());
        List<Transaction> transactions = parsedTransactions.getTransactions();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t1 = expectedSampleTransactions.get(i);
            Transaction t2 = transactions.get(i);
            // Hibernate may be setting the ID explicitly if another test has stored the transactions into the repository
            // ensure test doesnt fail on the ID field, which was not set by the transaction parser
            t2.setId(t1.getId());
            assertEquals(t1, t2);
        }
    }

}
