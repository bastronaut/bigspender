package com.bastronaut.bigspender.integration;


import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.SampleData;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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

    final private String userpw = TEST_EMAIL + ":" + TEST_PASSWORD;
    final private String headerEncoded = "Basic " + (Base64.getEncoder().encodeToString(userpw.getBytes()));
    private String userid;
    private List<Transaction> transactions;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup initial user for various user related tests
        final User testuser = SampleData.getTestUser();
        userRepository.save(testuser);

        // Setup sample transactions for validation
        this.transactions = SampleData.getTransactions();
        transactionRepository.saveAll(transactions);

        // Resources are often queried by the user id (in endpoints), we must find the exact user id to set correct resource paths
        final Optional<User> optionalUser = userRepository.findByEmail(TEST_EMAIL);
        userid = String.valueOf(optionalUser.get().getId());
    }


    @Test
    public void testAddTransactionForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param("date", "2019-04-07")
                .param("time", "07:25:00")
                .param("name" , "Test transaction")
                .param("accountNumber" , "NL20INGB0004567891")
                .param("receivingAccountNumber" , "NL20INGB0001987654")
                .param("code" , "BA")
                .param("type" , "BIJ")
                .param("amount" , "1980")
                .param("mutationType" , "DIVERSEN")
                .param("statement" , "Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019")
                .param("day" , "SUNDAY"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2019-04-07"))
                .andExpect(jsonPath("$.time").value("07:25:00"))
                .andExpect(jsonPath("$.name").value("Test transaction"))
                .andExpect(jsonPath("$.accountNumber").value("NL20INGB0004567891"))
                .andExpect(jsonPath("$.receivingAccountNumber").value("NL20INGB0001987654"))
                .andExpect(jsonPath("$.type").value("BIJ"))
                .andExpect(jsonPath("$.amount").value("1980"))
                .andExpect(jsonPath("$.day").value("SUNDAY"));
    }

    @Test
    public void testGetTransactionsForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded))
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

    // Annotation required for thread safety https://stackoverflow.com/questions/32269192/spring-no-entitymanager-with-actual-transaction-available-for-current-thread
    @Transactional
    @Test
    public void testDeleteAllTransactionsForUser() throws Exception {
        final String deleteEndpoint = TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userid);
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncoded))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(transactions.size()));
    }

    @Transactional
    @Test
    public void testDeleteMultipleTransactionsForUser() throws Exception {
        final String deleteEndpoint = TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userid);
        final String[] transactionDeleteIds = new String[3];
        final String txId1 = String.valueOf(transactions.get(0).getId());
        final String txId2 = String.valueOf(transactions.get(2).getId());
        final String txId3 = String.valueOf(transactions.get(4).getId());

        transactionDeleteIds[0] = txId1;
        transactionDeleteIds[1] = txId2;
        transactionDeleteIds[2] = txId3;

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param("transactionIds", transactionDeleteIds)
                .param("deleted", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(transactions.size()))
                .andExpect(jsonPath("$.transactions[0]").value("1"))
                .andExpect(jsonPath("$.transactions[1]").value("2"))
                .andExpect(jsonPath("$.transactions[2]").value("3"))
                .andExpect(jsonPath("$.transactions[3]").value("4"))
                .andExpect(jsonPath("$.transactions[4]").value("5"))
                .andExpect(jsonPath("$.transactions[5]").value("6"));
    }

    @Transactional
    @Test
    public void testDeleteTransactionForUser() throws Exception {
        final Transaction firstTransaction = transactions.get(0);
        final String firstTransactionId = String.valueOf(firstTransaction.getId());
        final String deleteEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userid).replace(TRANSACTIONID_PARAM_REPLACE, firstTransactionId);

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncoded))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Second run should be a 404 for nonexisting resource as the resource has been deleted
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncoded))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistingTransaction() throws Exception {
        final String nonExistingResource = "9999987654321";
        final String deleteEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userid).replace(TRANSACTIONID_PARAM_REPLACE, nonExistingResource);

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncoded))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
