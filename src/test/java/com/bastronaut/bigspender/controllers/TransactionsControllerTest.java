package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.services.TransactionService;
import com.bastronaut.bigspender.utils.JsonResponseUtil;
import com.bastronaut.bigspender.utils.SampleData;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;





@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class TransactionsControllerTest {

    private static final String GET_TRANSACTION_ENDPOINT = "/users/1/transactions/2";
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

        this.input = new FileInputStream(sampleFile);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        given(transactionService.getTransactionForUser(any(), anyLong())).willReturn(optionalTransaction);
    }

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(transactionsController);

    }

    @WithMockUser
    @Test
    public void testRetrieveUserTransaction() throws Exception {

        final MvcResult request = mockMvc.perform(MockMvcRequestBuilders.get("/users/15/transactions/1245"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("type").value("AF"))
                .andExpect(jsonPath("code").value("GT"))
                .andExpect(jsonPath("accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("name").value("AH to go 5869 DenHaa"))
                .andExpect(jsonPath("amount").value(180))
                .andExpect(jsonPath("mutationType").value("BETAALAUTOMAAT"))
                .andExpect(jsonPath("statement").value("Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019"))
                .andExpect(jsonPath("day").value("MONDAY"))
                .andExpect(jsonPath("time").value("22:39:00"))
                .andExpect(jsonPath("date").value("2019-04-01"))
                .andExpect(jsonPath("id").value("0"))
                .andReturn();
    }


    @Test
    public void testRetrieveUserTransactions() {
        assert(false);
    }

    @Test
    public void testRetrieveUsersTransactionsNoResult() {
        assert(false);
    }

    @Test
    public void testDeleteTransaction() {
        assert(false);
    }

    @Test
    public void testDeleteTransactions() {
        assert(false);
    }


}