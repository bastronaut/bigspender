package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.exceptions.RegistrationException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_LOGIN_ENDPOINT = "/users/login";

    private static final String NAME_PARAM = "name";
    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_FIRSTNAME = "tester";
    private static final String TEST_PASSWORD = "testpassword";

    private static final String USER_EXISTS_MESSAGE = "User already exists: " + TEST_EMAIL;

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
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(NAME_PARAM, TEST_FIRSTNAME)
                .param(PASSWORD_PARAM, TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response =  result.getResponse();
        String createUserResponse = response.getContentAsString();
        assert(StringUtils.contains(createUserResponse, TEST_EMAIL));
        assert(StringUtils.contains(createUserResponse, TEST_FIRSTNAME));
    }

    @Test(expected = RegistrationException.class)
    public void testUserExists() throws Exception {
        performUserRegistration("existing@email.com", "testy", "testpw");
        final MockHttpServletResponse response  = performUserRegistration("existing@email.com", "testy", "testpw").getResponse();
        assertEquals(response.getStatus(), 400);
        assertTrue(StringUtils.contains(USER_EXISTS_MESSAGE, response.getErrorMessage()));
    }

    public MvcResult performUserRegistration(final String email, final String name, final String password) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(NAME_PARAM, email)
                .param(EMAIL_PARAM, name)
                .param(PASSWORD_PARAM, password))
                .andReturn();
    }

}