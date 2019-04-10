package com.bastronaut.bigspender.controllers;

import org.apache.commons.lang3.StringUtils;
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

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_FIRSTNAME = "tester";

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
                .param("email", TEST_EMAIL)
                .param("firstName", TEST_FIRSTNAME)
                .param("password", "testpassword"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response =  result.getResponse();
        String createUserResponse = response.getContentAsString();
        assert(StringUtils.contains(createUserResponse, TEST_EMAIL));
        assert(StringUtils.contains(createUserResponse, TEST_FIRSTNAME));
    }

    @Test
    public void login() {
    }

    @Test
    public void testSecuredLogin() {

    }
}