package com.bastronaut.bigspender.services;


import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionMutationType;
import com.bastronaut.bigspender.models.TransactionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.bastronaut.bigspender.models.TransactionMutationType.BETAALAUTOMAAT;
import static com.bastronaut.bigspender.models.TransactionMutationType.DIVERSEN;
import static com.bastronaut.bigspender.models.TransactionMutationType.ONLINEBANKIEREN;
import static com.bastronaut.bigspender.models.TransactionType.AF;
import static com.bastronaut.bigspender.models.TransactionType.BIJ;
import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static com.bastronaut.bigspender.utils.TestConstants.SUBSET_SAMPLE_CSV_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
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
        Transaction t1 = new Transaction(LocalDate.of(2019, 04, 01),
                LocalTime.of(22,39), "AH to go 5869 DenHaa", "NL41INGB0006212385",
                null, "BA", AF, 180, BETAALAUTOMAAT,
                "Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019", DayOfWeek.MONDAY);

        Transaction t2 = new Transaction(LocalDate.of(2019, 04, 02),
                LocalTime.of(02,39), "AH to go 5869 DenHaa", "NL41INGB0006451386",
                null, "BA", AF, 1180, BETAALAUTOMAAT,
                "Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019", DayOfWeek.TUESDAY);

        Transaction t3 = new Transaction(LocalDate.of(2019, 04, 03),
                LocalTime.of(14,15), "AH to go", "NL20INGB0001234567",
                "NL20INGB0007654321", "BA", AF, 1980, ONLINEBANKIEREN,
                "Pasvolgnr: 008 01-04-2019 14:15 Valutadatum: 02-04-2019", DayOfWeek.WEDNESDAY);

        Transaction t4 = new Transaction(LocalDate.of(2019, 04, 04),
                LocalTime.of(16,15), "to go", "NL20INGB0001234567",
                "NL20INGB0007654321", "BA", AF, 5121, ONLINEBANKIEREN,
                "Pasvolgnr: 008 01-04-2019 16:15 Valutadatum: 02-04-2019", DayOfWeek.THURSDAY);

        Transaction t5 = new Transaction(LocalDate.of(2019, 04, 05),
                LocalTime.of(23,01), "AH to go Den", "",
                "NL20INGB0007654321", "BA", AF, 14120, DIVERSEN,
                "Pasvolgnr: 008 01-04-2019 23:01 Valutadatum: 02-04-2019", DayOfWeek.FRIDAY);

        Transaction t6 = new Transaction(LocalDate.of(2019, 04, 06),
                null, "AH to go go", "",
                "NL20INGB0007654321", "BA", AF, 999999, ONLINEBANKIEREN,
                "Pasvolgnr: 008 01-04-2019 Valutadatum: 02-04-2019", DayOfWeek.SATURDAY);

        Transaction t7 = new Transaction(LocalDate.of(2019, 04, 07),
                LocalTime.of(22,39), "test to go go yes", "NL20INGB0004567891",
                "NL20INGB0001987654", "BA", BIJ, 1980, DIVERSEN,
                "Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019", DayOfWeek.SUNDAY);

        expectedSampleTransactions.add(t1);
        expectedSampleTransactions.add(t2);
        expectedSampleTransactions.add(t3);
        expectedSampleTransactions.add(t4);
        expectedSampleTransactions.add(t5);
        expectedSampleTransactions.add(t6);
        expectedSampleTransactions.add(t7);

        File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        this.input = new FileInputStream(sampleFile);
    }

//    TODO feitje remco: de transaction date komt niet overeen met de werkelijke transactie datum, maar waarschijnlijk soort van transactie resolutie / consolidatie
    @Test
    public void testParseTransactions() {

        List<Transaction> parsedTransactions = importer.parseTransactions(input);
        assertEquals("Seven transactions have been parsed", 7, parsedTransactions.size());
        for (int i = 0; i < parsedTransactions.size(); i++) {
            assertEquals(parsedTransactions.get(i), expectedSampleTransactions.get(i));
        }
    }


}
