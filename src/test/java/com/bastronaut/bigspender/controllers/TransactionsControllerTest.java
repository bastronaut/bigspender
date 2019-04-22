package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.utils.JsonResponseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringSecurityWebTestConfig.class)
@AutoConfigureMockMvc
public class TransactionsControllerTest {

    private static final String GET_TRANSACTION_ENDPOINT = "/users/{userid}/transactions/{transactionid}";
    private static final String GET_TRANSACTIONS_ENDPOINT = "/users/{userid}/transactions";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionController transactionsController;


    private List<Transaction> expectedSampleTransactions;
    private FileInputStream input;

    @Before
    public void setupSampleData() throws FileNotFoundException {
        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        this.input = new FileInputStream(sampleFile);
    }

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(transactionsController);

    }

    @WithMockUser("user@company.com")
    @Test
    public void testRetrieveUserTransactions() throws Exception {



//        final MvcResult request = mockMvc.perform(MockMvcRequestBuilders.get(GET_TRANSACTION_ENDPOINT))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        final MockHttpServletResponse response = request.getResponse();
//        final String retrieveTransactionsResponse = response.getContentAsString();
//        final JsonNode result = JsonResponseUtil.getJsonFromResponseContent(retrieveTransactionsResponse);
//        assertEquals(response.getStatus(), HttpStatus.OK.value());
//        assertTrue(StringUtils.contains(retrieveTransactionsResponse, "some tx information"));
    }


    @Test
    public void testRetrieveUserTransaction() {
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