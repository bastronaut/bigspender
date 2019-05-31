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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
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
public class TransactionsControllerTest {

    private static final String GET_TRANSACTION_ENDPOINT = "/users/1/transactions/2";
    private static final String DELETE_TRANSACTION_ENDPOINT = "/users/1/transactions/1";
    private static final String GET_TRANSACTIONS_ENDPOINT = "/users/1/transactions";

    @Autowired
    private TransactionController transactionsController;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private WebApplicationContext context;

    private List<Transaction> expectedSampleTransactions;
    private FileInputStream input;
    private MockMvc mockMvc;

    @Before
    public void setup() throws FileNotFoundException {
        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        final Transaction testTransaction = SampleData.t1;
        final Optional<Transaction> optionalTransaction = Optional.of(testTransaction);

        final List<Transaction> testTransactions = SampleData.getTransactions();

        this.input = new FileInputStream(sampleFile);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        given(transactionService.getTransactionForUser(anyLong(), any())).willReturn(optionalTransaction);
        given(transactionService.getTransactionsForUser(any())).willReturn(testTransactions);
        // Returns different result after the first result with varargs argument
        given(transactionService.deleteUserTransaction(anyLong(), any())).willReturn(1L, 0L);
    }

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(transactionsController);

    }

    @WithMockUser
    @Test
    public void testRetrieveUserTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_TRANSACTION_ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("type").value("AF"))
                .andExpect(jsonPath("code").value("GT"))
                .andExpect(jsonPath("accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("name").value("AH to go 5869 DenHaa"))
                .andExpect(jsonPath("amount").value(180))
                .andExpect(jsonPath("mutationType").value("Betaalautomaat"))
                .andExpect(jsonPath("statement").value("Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019"))
                .andExpect(jsonPath("day").value("Monday"))
                .andExpect(jsonPath("time").value("22:39:00"))
                .andExpect(jsonPath("date").value("2019-04-01"))
                .andExpect(jsonPath("id").value("0"))
                .andReturn();
    }

    @WithMockUser
    @Test
    public void testRetrieveUserTransactions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(GET_TRANSACTIONS_ENDPOINT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("$[0].type").value("AF"))
                .andExpect(jsonPath("$[6].accountNumber").value("NL20INGB0004567891"))
                .andExpect(jsonPath("$[6].name").value("test to go go yes"));
    }

//    @Test
//    public void testRetrieveUsersTransactionsNoResult() {
//        assert(false);
//    }
//
    @WithMockUser
    @Test
    public void testDeleteTransaction() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_TRANSACTION_ENDPOINT))
                .andDo(print())
                .andExpect(status().isNoContent());

        // second run should be a 404 for nonexisting resource, so 2nd time deleting the resource
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_TRANSACTION_ENDPOINT))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
//
//    @Test
//    public void testDeleteTransactions() {
//        assert(false);
//    }


}