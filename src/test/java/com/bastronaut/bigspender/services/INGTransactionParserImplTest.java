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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // required to reset state after test
@SpringBootTest
public class INGTransactionParserImplTest {

    @Autowired
    private INGTransactionParserImpl importer;

    private SampleData sampleData = new SampleData();

    private final List<Transaction> expectedSampleTransactions  = sampleData.getTransactions();

    private FileInputStream input;
    private final User testUser = sampleData.getTestUserOne();


    @Test
    public void testParseTransactions() throws IOException {

        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        this.input = new FileInputStream(sampleFile);

        final TransactionImport parsedTransactions = importer.parseTransactions(input, testUser);

        assertEquals("Seven transactions have been parsed", 7, parsedTransactions.getTransactions().size());
        assertEquals("Import count has been properly set", 7, parsedTransactions.getImportCount());

        List<Transaction> transactions = parsedTransactions.getTransactions();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t1 = expectedSampleTransactions.get(i);
            Transaction t2 = transactions.get(i);
            // HACK: normally, JPA is responsible for setting Id on the entity. However, as we're only testing the
            // parsing and we're not creating entities out of it, its not setting the id field and a 0 id will fail
            // the equals. Therefor, explicitly set id
            long random = new Random().nextLong();
            t1.setId(random);
            t2.setId(random);
            assertEquals(t1, t2);
        }
    }

}
