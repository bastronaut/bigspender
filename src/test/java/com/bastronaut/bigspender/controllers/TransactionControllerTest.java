package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.services.TransactionService;
import com.bastronaut.bigspender.utils.SampleData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.SampleData.HEADER_ENCODED_NONEXISTINGUSER;
import static com.bastronaut.bigspender.utils.SampleData.HEADER_ENCODED_USERTWO;
import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_ENDPOINT;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // required to reset state after test
public class TransactionControllerTest {

    @Autowired
    private TransactionController transactionsController;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private WebApplicationContext context;

    private FileInputStream input;
    private MockMvc mockMvc;

    private SampleData sampleData = new SampleData();

    @Before
    public void setup() throws FileNotFoundException {
        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        final Transaction testTransaction = sampleData.t1;
        final Optional<Transaction> optionalTransaction = Optional.of(testTransaction);

        final List<Transaction> testTransactions = sampleData.getTransactions();

        this.input = new FileInputStream(sampleFile);

        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        given(transactionService.getTransactionForUser(anyLong(), any())).willReturn(optionalTransaction);
        given(transactionService.getTransactionsForUser(any())).willReturn(testTransactions);
        // Returns different result after the first result with varargs argument
        given(transactionService.deleteTransactionForUser(anyLong(), any())).willReturn(1L, 0L);
    }

    @WithMockUser
    @Test
    public void testRetrieveUserTransaction() throws Exception {
        final String endpoint = TRANSACTION_ENDPOINT.replace(TRANSACTIONID_PARAM_REPLACE, "1");
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print())
                .andExpect(jsonPath("type").value("Af"))
                .andExpect(jsonPath("code").value("GT"))
                .andExpect(jsonPath("accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("name").value("AH to go 5869 DenHaa"))
                .andExpect(jsonPath("amount").value(180))
                .andExpect(jsonPath("mutationType").value("Betaalautomaat"))
                .andExpect(jsonPath("statement").value("Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019"))
                .andExpect(jsonPath("time").value("22:39:00"))
                .andExpect(jsonPath("date").value("2019-04-01"))
                .andExpect(jsonPath("id").isNotEmpty()) // ordinarily ID is set by hibernate, because of mocking this never happens so remains at 0
                .andReturn();
    }

    @WithMockUser
    @Test
    public void testRetrieveUserTransactions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("$[0].type").value("Af"))
                .andExpect(jsonPath("$[6].accountNumber").value("NL20INGB0004567891"))
                .andExpect(jsonPath("$[6].name").value("test to go go yes"));
    }

    @WithMockUser
    @Test
    public void testDeleteTransaction() throws Exception {
        final String endpoint = TRANSACTION_ENDPOINT.replace(TRANSACTIONID_PARAM_REPLACE, "1");
        mockMvc.perform(MockMvcRequestBuilders.delete(endpoint))
                .andDo(print())
                .andExpect(status().isNoContent());

        // second run should be a 404 for nonexisting resource, so 2nd time deleting the resource
        mockMvc.perform(MockMvcRequestBuilders.delete(endpoint))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    /**
     * Attempts to perform deletion with incorrect auth and missing auth. @Transactional to avoid adding too many
     * login attempts and triggering a refused login because of account locked
     * @throws Exception
     */
    @Transactional
    @Test
    public void testGetTransactionNotAuthorized() throws Exception {

        final String endpoint = TRANSACTION_ENDPOINT.replace(TRANSACTIONID_PARAM_REPLACE, "1");
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    /**
     * Attempts to perform deletion with incorrect auth and missing auth. @Transactional to avoid adding too many
     *      * login attempts and triggering a refused login because of account locked
     * @throws Exception
     */
    @Transactional
    @Test
    public void testGetTransactionsNotAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // todo verify why going into 403 instead of 401
        mockMvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    /**
     * Attempts to perform deletion with incorrect auth and missing auth
     * @throws Exception
     */
    @Transactional
    @Test
    public void testDeleteTransactionsNotAuthorized() throws Exception {
        final String deleteEndpoint = TRANSACTIONS_ENDPOINT;
        final String[] transactionDeleteIds = new String[4];
        final String txId1 = String.valueOf("1");
        final String txId2 = String.valueOf("2");

        transactionDeleteIds[0] = txId1;
        transactionDeleteIds[1] = txId2;

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .param("transactionIds", transactionDeleteIds))
                .andDo(print())
                .andExpect(status().isUnauthorized());


        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .param("transactionIds", transactionDeleteIds)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testAddTransactionNotAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS_ENDPOINT)
                .param("date", "2019-04-07")
                .param("time", "07:25:00")
                .param("name" , "Test transaction")
                .param("accountNumber" , "NL20INGB0004567891")
                .param("receivingAccountNumber" , "NL20INGB0001987654")
                .param("code" , "BA")
                .param("type" , "BIJ")
                .param("amount" , "1980")
                .param("mutationType" , "Diversen")
                .param("statement" , "Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS_ENDPOINT)
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
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }






}