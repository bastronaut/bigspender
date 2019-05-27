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
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.bastronaut.bigspender.utils.SampleData.getTestUser;
import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class INGTransactionParserImplTest {

    @Autowired
    INGTransactionParserImpl importer;

    private final List<Transaction> expectedSampleTransactions  = SampleData.getTransactions();

    private FileInputStream input;
    private final User testUser = getTestUser();

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
            assertTrue(isEqualTransaction(t1, t2));
        }
    }

    // Because we store the SampleData into the repository in another test, it will assign IDs to the sample transactions.
    // The parsed transactions will not have IDs assigned, so the equal comparison will fail. We will have to create
    // a custom comparison without the ID field here.
    // Not great, but alternative is setting up additional sample TX data, probably a TODO
    // TODO do it, this is nasty and no good error loggin where assertion failed. prolly just setup new sample txs
    private boolean isEqualTransaction(final Transaction t1, final Transaction t2) {
        return
                StringUtils.equals(t1.getName(), t2.getName()) &&
                StringUtils.equals(t1.getAccountNumber(), t2.getAccountNumber()) &&
                StringUtils.equals(t1.getReceivingAccountNumber(), t2.getReceivingAccountNumber()) &&
                t1.getAmount() == t2.getAmount() &&
                t1.getCode().equals(t1.getCode()) &&
                t1.getDate() == t2.getDate() &&
                t1.getTime().equals(t2.getTime()) &&
                t1.getDay().equals(t2.getDay()) &&
                t1.getMutationType().equals(t2.getMutationType()) &&
                StringUtils.equals(t1.getStatement(), t2.getStatement()) &&
                t1.getUser() == t2.getUser() &&
                t1.getType().equals(t2.getType());

    }


}
