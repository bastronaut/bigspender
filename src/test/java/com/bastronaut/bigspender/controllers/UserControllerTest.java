package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.BigspenderApplication;
import com.bastronaut.bigspender.config.SecurityConfiguration;
import com.bastronaut.bigspender.exceptions.UserRegistrationException;
import com.bastronaut.bigspender.exceptions.UserUpdateException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class UserControllerTest  {
    //extends AbstractTransactionalJUnit4SpringContextTests
    private static final String USERS_ENDPOINT = "/users";
    private static final String USERS_UPDATE_ENDPOINT = "/users/1";
    private static final String USERS_GET_INFO_ENDPOINT = "/users/1";

    private static final String NAME_PARAM = "name";
    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_FIRSTNAME = "tester";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String BASICAUTH_USERPW = TEST_EMAIL + ":" + TEST_PASSWORD;
    private static final String BASE64_BASICAUTH_USERPW = "Basic " + Base64.getEncoder().encodeToString(BASICAUTH_USERPW.getBytes());

    private static final String TEST_EMAIL_UPDATE = "update@email.com";
    private static final String TEST_FIRSTNAME_UPDATE = "updated";
    private static final String TEST_PASSWORD_UPDATE = "updated";

    private static final String USER_EXISTS_MESSAGE = "User already exists: " + TEST_EMAIL;

    private static final String AUTHORIZATION_HEADER = "Authorization";

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
        MvcResult result = performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME, TEST_PASSWORD);
        assertTrue(result.getResponse().getStatus() == HttpStatus.OK.value());
        MockHttpServletResponse response =  result.getResponse();
        String createUserResponse = response.getContentAsString();
        assert(StringUtils.contains(createUserResponse, TEST_EMAIL));
        assert(StringUtils.contains(createUserResponse, TEST_FIRSTNAME));
    }

    @Test(expected = UserRegistrationException.class)
    public void registerExistingUserError() throws Exception {
        performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME, TEST_PASSWORD);
        final MockHttpServletResponse response  = performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME,
                TEST_PASSWORD).getResponse();
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
        assertTrue(StringUtils.contains(USER_EXISTS_MESSAGE, response.getErrorMessage()));
    }

    @Test
//    @WithMockUser(username = TEST_EMAIL, password = TEST_PASSWORD, roles = "USER")
    public void testUpdateUser() throws Exception {
        performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME, TEST_PASSWORD);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(USERS_UPDATE_ENDPOINT)
//                .with(httpBasic(TEST_EMAIL, TEST_PASSWORD))
//                .header(AUTHORIZATION_HEADER, BASE64_BASICAUTH_USERPW)
                .param(EMAIL_PARAM, TEST_EMAIL_UPDATE)
                .param(NAME_PARAM, TEST_FIRSTNAME_UPDATE)
                .param(PASSWORD_PARAM, TEST_PASSWORD_UPDATE))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        final MockHttpServletResponse response = result.getResponse();
        final String updateUserResponse = response.getContentAsString();
        assert(StringUtils.contains(updateUserResponse, TEST_EMAIL_UPDATE ));
        assert(StringUtils.contains(updateUserResponse, TEST_FIRSTNAME_UPDATE ));
    }

    @Test(expected = UserUpdateException.class)
    public void testUpdateUserInvalid() throws Exception {
        MvcResult registration = performUserRegistration(TEST_EMAIL, TEST_FIRSTNAME, TEST_PASSWORD);
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(USERS_UPDATE_ENDPOINT)
                .header(AUTHORIZATION_HEADER, BASE64_BASICAUTH_USERPW))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }


    @Test
    public void testNotAuthorizedGetUserInfo() throws Exception {
        // todo
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_GET_INFO_ENDPOINT).with(anonymous()));
        assert(false);
    }

    @Test
    public void testNotAuthorizedUpdateUserInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_UPDATE_ENDPOINT).with(anonymous()));
        assert(false);
    }


    public MvcResult performUserRegistration(final String email, final String name, final String password) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(NAME_PARAM, name)
                .param(EMAIL_PARAM, email)
                .param(PASSWORD_PARAM, password))
                .andDo(print())
                .andReturn();
    }

}