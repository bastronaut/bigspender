package com.bastronaut.bigspender.integration;


import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.SampleData;
import javassist.tools.rmi.Sample;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.USERID_PARAM_REPLACE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // required to reset state after test
@AutoConfigureMockMvc
@ContextConfiguration
public class TransactionIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;

    final private String headerEncodedUserOne = SampleData.HEADER_ENCODED_USERONE;
    final private String headerEncodedUserTwo = SampleData.HEADER_ENCODED_USERTWO;

    private String userId;
    private String userIdTestuserTwo;
    private List<Transaction> transactions;
    private Transaction testUserTwoTransaction;

    final private User testUserOne = SampleData.TESTUSERONE;
    final private ≠User testUserTwo = SampleData.TESTUSERTWO;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup initial users for various user related tests
        userRepository.save(testUserOne);
        userRepository.save(testUserTwo);

        userId = String.valueOf(testUserOne.getId());
        userIdTestuserTwo = String.valueOf(testUserTwo.getId());

        // Setup sample transactions for validation
        this.transactions = SampleData.getTransactions();

        this.testUserTwoTransaction = SampleData.t1TESTUSERTWO;

        transactionRepository.saveAll(this.transactions);
        transactionRepository.save(this.testUserTwoTransaction);
    }


    @Test
    public void testAddTransactionForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userId))
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .param("date", "2019-04-07")
                .param("time", "07:25:00")
                .param("name" , "Test transaction")
                .param("accountNumber" , "NL20INGB0004567891")
                .param("receivingAccountNumber" , "NL20INGB0001987654")
                .param("code" , "BA")
                .param("type" , "BIJ")
                .param("amount" , "1980")
                .param("mutationType" , "Diversen")
                .param("statement" , "Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019")
                .param("day" , "7"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2019-04-07"))
                .andExpect(jsonPath("$.time").value("07:25:00"))
                .andExpect(jsonPath("$.name").value("Test transaction"))
                .andExpect(jsonPath("$.accountNumber").value("NL20INGB0004567891"))
                .andExpect(jsonPath("$.receivingAccountNumber").value("NL20INGB0001987654"))
                .andExpect(jsonPath("$.type").value("Bij"))
                .andExpect(jsonPath("$.amount").value("1980"))
                .andExpect(jsonPath("$.day").value(7));
    }

    @Test
    public void testGetTransactionsForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userId))
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("$.[1].accountNumber").value("NL41INGB0006451386"))
                .andExpect(jsonPath("$.[2].accountNumber").value("NL20INGB0001234567"))
                .andExpect(jsonPath("$.[3].accountNumber").value("NL20INGB0001234567"))
                .andExpect(jsonPath("$.[4].accountNumber").value("NL20INGB0002345678"))
                .andExpect(jsonPath("$.[5].accountNumber").value("NL20INGB0003456789"))
                .andExpect(jsonPath("$.[6].accountNumber").value("NL20INGB0004567891"));
    }

    // TODO service is wrong
    @Test
    public void testGetTransactionForUser() throws Exception {
        final Transaction tx1 = transactions.get(1);
        final long txid = tx1.getId();
        final DateTimeFormatter dtf = SampleData.dtf;
        final String getTransactionEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userId)
                .replace(TRANSACTIONID_PARAM_REPLACE, String.valueOf(txid));

        mockMvc.perform(MockMvcRequestBuilders.get(getTransactionEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber").value(tx1.getAccountNumber()))
                .andExpect(jsonPath("$.receivingAccountNumber").value(tx1.getReceivingAccountNumber()))
                .andExpect(jsonPath("$.statement").value(tx1.getStatement()))
                .andExpect(jsonPath("$.day").value(tx1.getDay().getValue()))
                .andExpect(jsonPath("$.amount").value(String.valueOf(tx1.getAmount())))
                .andExpect(jsonPath("$.mutationType").value(tx1.getMutationType().getType()))
                .andExpect(jsonPath("$.code").value(tx1.getCode().getType()))
                .andExpect(jsonPath("$.name").value(tx1.getName()))
                .andExpect(jsonPath("$.time").value(tx1.getTime().format(dtf).toString()))
                .andExpect(jsonPath("$.id").value(String.valueOf(tx1.getId())))
                .andExpect(status().isOk());

    }

    // Annotation required for thread safety https://stackoverflow.com/questions/32269192/spring-no-entitymanager-with-actual-transaction-available-for-current-thread
    @Transactional
    @Test
    public void testDeleteTransactionsForUserNoTransactionIds() throws Exception {
        final String deleteEndpoint = TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userId);
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Transaction error"))
                .andExpect(jsonPath("$.details").value("No transaction IDs to delete specified"));
    }

    @Transactional
    @Test
    public void testDeleteMultipleTransactionsForUser() throws Exception {
        final String deleteEndpoint = TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userId);
        final String[] transactionDeleteIds = new String[4];
        final String txId1 = String.valueOf(transactions.get(0).getId());
        final String txId2 = String.valueOf(transactions.get(2).getId());
        final String txId3 = String.valueOf(transactions.get(4).getId());

        transactionDeleteIds[0] = txId1;
        transactionDeleteIds[1] = txId2;
        transactionDeleteIds[2] = txId3;
        transactionDeleteIds[3] = "9858213821";

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .param("transactionIds", transactionDeleteIds))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(3))
                .andExpect(jsonPath("$.transactionIds[0]").value(txId1))
                .andExpect(jsonPath("$.transactionIds[1]").value(txId2))
                .andExpect(jsonPath("$.transactionIds[2]").value(txId3));
    }

    @Transactional
    @Test
    public void testDeleteTransactionForUser() throws Exception {
        final Transaction firstTransaction = transactions.get(0);
        final String firstTransactionId = String.valueOf(firstTransaction.getId());
        final String deleteEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userId).replace(TRANSACTIONID_PARAM_REPLACE, firstTransactionId);

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Second run should be a 404 for nonexisting resource as the resource has been deleted
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistingTransaction() throws Exception {
        final String nonExistingResource = "9999987654321";
        final String deleteEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userId).replace(TRANSACTIONID_PARAM_REPLACE, nonExistingResource);

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddTransactionNotAuthorized() throws Exception {

    }

    @Test
    public void testDeleteTransactionNotAuthorized() throws Exception {
        final Transaction firstTransaction = transactions.get(0);
        final String firstTransactionId = String.valueOf(firstTransaction.getId());
        final String deleteEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userId).replace(TRANSACTIONID_PARAM_REPLACE, firstTransactionId);

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    /**
     * Attempt to delete a transaction from another user by userid and transaction id
     * @throws Exception
     */
    @Test
    public void testDeleteTransactionOtherUser() throws Exception {

        final String userOneTransactionId = String.valueOf(this.transactions.get(0).getId());

        final String userTwoTransactionId = String.valueOf(this.testUserTwoTransaction.getId());
        final String deleteEndpointTwo = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userIdTestuserTwo)
                .replace(TRANSACTIONID_PARAM_REPLACE, userTwoTransactionId);

        // Using authorization header for User 1, attempt to delete resource from User 2
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpointTwo)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // TODO set auth headers
        assertTrue(false);
        // Using authorization header for User 2, attempt to delete resource from User 1
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpointTwo)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserTwo))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testDeleteTransactionsNotAuthorized() throws Exception {
        final String deleteEndpoint = TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userId);
        final String[] transactionDeleteIds = new String[4];
        final String txId1 = String.valueOf(transactions.get(0).getId());
        final String txId2 = String.valueOf(transactions.get(2).getId());

        transactionDeleteIds[0] = txId1;
        transactionDeleteIds[1] = txId2;

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .param("transactionIds", transactionDeleteIds))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetTransactionNotAuthorized() throws Exception {

    }

    @Test
    public void testGetTransactiosnNotAuthorized() throws Exception {

    }


}
