package com.bastronaut.bigspender.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;

//import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionController transactionsController;

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(transactionsController);

    }

    @Test
    public void testRetrieveUsersTransactions() {
        MockMvc result = mockMvc.perform(MockMvcRequestBuilders.get())
                .andExpect()
                .andReturn()
    }

    @Test
    public void testRetrieveUsersTransactionsNoResult() {

    }



}