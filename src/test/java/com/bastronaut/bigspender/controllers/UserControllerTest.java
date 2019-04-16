package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.exceptions.RegistrationException;
import com.bastronaut.bigspender.exceptions.UserRegistrationException;
import com.bastronaut.bigspender.exceptions.UserUpdateException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_LOGIN_ENDPOINT = "/users/login";
    private static final String USERS_UPDATE_ENDPOINT = "/users/1";

    private static final String NAME_PARAM = "name";
    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_FIRSTNAME = "tester";
    private static final String TEST_PASSWORD = "testpassword";

    private static final String TEST_EMAIL_UPDATE = "update@email.com";
    private static final String TEST_FIRSTNAME_UPDATE = "updated";
    private static final String TEST_PASSWORD_UPDATE = "updated";

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

    @Test(expected = UserRegistrationException.class)
    public void registerUserExists() throws Exception {
        performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME, TEST_PASSWORD);
        final MockHttpServletResponse response  = performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME,
                TEST_PASSWORD).getResponse();
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertTrue(StringUtils.contains(USER_EXISTS_MESSAGE, response.getErrorMessage()));
    }

    @Test
    public void testUpdateUser() throws Exception {
        performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME, TEST_PASSWORD);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(USERS_UPDATE_ENDPOINT)
                .param(EMAIL_PARAM, TEST_EMAIL_UPDATE)
                .param(NAME_PARAM, TEST_FIRSTNAME_UPDATE)
                .param(PASSWORD_PARAM, TEST_PASSWORD_UPDATE))
                .andExpect(status().isOk())
                .andReturn();

        final MockHttpServletResponse response = result.getResponse();
        final String updateUserResponse = response.getContentAsString();
        assert(StringUtils.contains(updateUserResponse, TEST_EMAIL_UPDATE ));
        assert(StringUtils.contains(updateUserResponse, TEST_FIRSTNAME_UPDATE ));
    }

    @Test(expected = UserUpdateException.class)
    public void testUpdateUserInvalid() throws Exception {
        performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME, TEST_PASSWORD);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(USERS_UPDATE_ENDPOINT))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    public MvcResult performUserRegistration(final String email, final String name, final String password) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(NAME_PARAM, email)
                .param(EMAIL_PARAM, name)
                .param(PASSWORD_PARAM, password))
                .andReturn();
    }

}