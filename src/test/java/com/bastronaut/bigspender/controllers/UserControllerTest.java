package com.bastronaut.bigspender.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_LOGIN_ENDPOINT = "/users/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void contextLoads() {
        assertNotNull(userController);
    }

    @Test
    public void createUser() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param("email", "")
                .param("firstName", "test")
                .param("password", "testpassword"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response =  result.getResponse();
        String createUserResponse = response.getContentAsString();
    }

    @Test
    public void login() {
    }
}