package com.bastronaut.bigspender.controllers;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;

import static com.bastronaut.bigspender.utils.TestConstants.EMAIL_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.NAME_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.PASSWORD_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_FIRSTNAME;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_FIRSTNAME_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.USERS_UPDATE_ENDPOINT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class IntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserController userController;

    private MockMvc mockMvc;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Arrange - setup user to update
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(NAME_PARAM, TEST_FIRSTNAME)
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(PASSWORD_PARAM, TEST_PASSWORD))
                .andDo(print())
                .andReturn();

        final String userpw =  TEST_EMAIL + ":" + TEST_PASSWORD;
        final String headerEncoded = "Basic " + (Base64.getEncoder().encodeToString(userpw.getBytes()));

        // Update user, auth headers are required to inject the User argument into the controller
        mockMvc.perform(MockMvcRequestBuilders.put(USERS_UPDATE_ENDPOINT)
        .header(HttpHeaders.AUTHORIZATION, headerEncoded)
        .param(EMAIL_PARAM, TEST_EMAIL_UPDATE)
        .param(NAME_PARAM, TEST_FIRSTNAME_UPDATE))
                .andExpect(jsonPath(EMAIL_PARAM).value(TEST_EMAIL_UPDATE))
                .andExpect(jsonPath(NAME_PARAM).value(TEST_FIRSTNAME_UPDATE)).andReturn();


    }



}
