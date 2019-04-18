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
public class INGTransactionImporterImplTest {

    @Autowired
    INGTransactionImporterImpl importer;

    private List<Transaction> expectedSampleTransactions;

    private FileInputStream input;

    /**
     * Sample transactions.csv map to the following t1-t7:
     *
     * t1 "20190401","AH to go 5869 DenHaa","NL41INGB0006212385","","BA","Af","1,80","Betaalautomaat","Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019"
     * t2 "20190402","AH to go 5869 DenHaa","NL41INGB0006451386","","BA","Bij","11,80","Betaalautomaat","Pasvolgnr: 008 01-04-2019 02:39 Valutadatum: 02-04-2019"
     * t3 "20190403","AH to go","NL20INGB0001234567","NL20INGB0007654321","BA","Af","19,80","Online bankieren","Pasvolgnr: 008 01-04-2019 14:15 Valutadatum: 02-04-2019"
     * invalid "20190407","Invalid row missing column","NL20INGB0004567891","NL20INGB0001987654","BA","Bij","19,80","Online bankieren"
     * t4 "20190404","to go","NL20INGB0001234567","NL20INGB0007654321","BA","Af","51,21","Online bankieren","Pasvolgnr: 008 01-04-2019 16:15 Valutadatum: 02-04-2019"
     * t5 "20190405","AH to go Den","NL20INGB0002345678","NL20INGB0007654321","GT","Af","141,20","Diversen","Pasvolgnr: 008 01-04-2019 23:01 Valutadatum: 02-04-2019"
     * t6 "20190406","AH to go go","NL20INGB0003456789","NL20INGB0007654321","GT","Af","9999,99","Online bankieren","Pasvolgnr: 008 01-04-2019 Valutadatum: 02-04-2019"
     * t7 "20190407","test to go go yes","NL20INGB0004567891","NL20INGB0001987654","BA","Bij","19,80","Diversen","Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019"
     */
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
