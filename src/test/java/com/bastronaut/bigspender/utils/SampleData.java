package com.bastronaut.bigspender.utils;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;

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

public class SampleData {

    private static final String username = "test@email.com";

    private static final User TESTUSER = new User(username, "tester", "test");
    public static User getTestUser() { return TESTUSER; }
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
    public static final Transaction t1 = new Transaction(LocalDate.of(2019, 04, 01),
            LocalTime.of(22,39), "AH to go 5869 DenHaa", "NL41INGB0006212385",
            null, GT, AF, 180, BETAALAUTOMAAT,
            "Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019", TESTUSER);

    public static final Transaction t2 = new Transaction(LocalDate.of(2019, 04, 02),
            LocalTime.of(02,39), "AH to go 5869 DenHaa", "NL41INGB0006451386",
            null, BA, BIJ, 1180, BETAALAUTOMAAT,
            "Pasvolgnr: 008 01-04-2019 02:39 Valutadatum: 02-04-2019", TESTUSER);

    public static final Transaction t3 = new Transaction(LocalDate.of(2019, 04, 03),
            LocalTime.of(14,15), "AH to go", "NL20INGB0001234567",
            "NL20INGB0007654321", BA, AF, 1980, ONLINEBANKIEREN,
            "Pasvolgnr: 008 01-04-2019 14:15 Valutadatum: 02-04-2019", TESTUSER);

    public static final Transaction t4 = new Transaction(LocalDate.of(2019, 04, 04),
            LocalTime.of(16,15), "to go", "NL20INGB0001234567",
            "NL20INGB0007654321", BA, AF, 5121, ONLINEBANKIEREN,
            "Pasvolgnr: 008 01-04-2019 16:15 Valutadatum: 02-04-2019", TESTUSER);

    public static final Transaction t5 = new Transaction(LocalDate.of(2019, 04, 05),
            LocalTime.of(23,01), "AH to go Den", "NL20INGB0002345678",
            "NL20INGB0007654321", GT, BIJ, 14120, DIVERSEN,
            "Pasvolgnr: 008 01-04-2019 23:01 Valutadatum: 02-04-2019", TESTUSER);

    public static final Transaction t6 = new Transaction(LocalDate.of(2019, 04, 06),
            null, "AH to go go", "NL20INGB0003456789",
            "NL20INGB0007654321", GT, AF, 999999, ONLINEBANKIEREN,
            "Pasvolgnr: 008 01-04-2019 Valutadatum: 02-04-2019", TESTUSER);

    public static final Transaction t7 = new Transaction(LocalDate.of(2019, 04, 07),
            LocalTime.of(07,25), "test to go go yes", "NL20INGB0004567891",
            "NL20INGB0001987654", BA, BIJ, 1980, DIVERSEN,
            "Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019", TESTUSER);

    private static final List<Transaction> TRANSACTIONS = new ArrayList<>();


    private static final TransactionImport TRANSACTION_IMPORT = new TransactionImport(TRANSACTIONS, TESTUSER);
    public static final TransactionImport getTransactionImport() { return TRANSACTION_IMPORT; }

    public static List<Transaction> getTransactions() {
        if (TRANSACTIONS.isEmpty()) {
            TRANSACTIONS.add(t1);
            TRANSACTIONS.add(t2);
            TRANSACTIONS.add(t3);
            TRANSACTIONS.add(t4);
            TRANSACTIONS.add(t5);
            TRANSACTIONS.add(t6);
            TRANSACTIONS.add(t7);
        }
        return TRANSACTIONS;
    }


}
