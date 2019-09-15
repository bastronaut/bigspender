package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.CustomUserDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.bastronaut.bigspender.utils.TestConstants.EMAIL_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_INVALID_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_USER_EMAIL_NULL;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_USER_PW_NULL;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_USER_PW_SIZE;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DETAILS_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_MESSAGE_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.PASSWORD_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.REGISTRATION_ERROR_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.UPDATE_ERROR_MSG;
import static com.bastronaut.bigspender.utils.TestConstants.USERID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.USER_ENDPOINT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class UserControllerTest {


    private static final String INVALID_UPDATE_INFORMATION = "No updateable information provided";
    // Ordinarily we add the user ID as resource to the path, but can hardcode them in the test context
    private static final String HARDCODED_USER_UPDATE_ENDPOINT = USER_ENDPOINT.replace(USERID_PARAM_REPLACE, "1");
    private static final String HARDCODED_USER_GET_INFO_ENDPOINT = USER_ENDPOINT.replace(USERID_PARAM_REPLACE, "1");


    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        final User registerUser = new User(TEST_EMAIL, TEST_PASSWORD);
        final User updatedUser = new User(TEST_EMAIL_UPDATE, TEST_PASSWORD_UPDATE);

        given(userDetailsService.updateUser(any(), any())).willReturn(updatedUser);
        given(userDetailsService.loadUserByUsername(anyString())).willReturn(registerUser);
        given(userDetailsService.registerUser(any())).willReturn(registerUser);
        doNothing().when(userDetailsService).logUserIn(any(), anyString(), anyString());
    }

    @Test
    public void contextLoads() {
        assertNotNull(userController);
    }


    @Test
    public void createUser() throws Exception {
        MvcResult result = performUserRegistration(TEST_EMAIL, TEST_PASSWORD);
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }


    @Test
    public void testCreateUserInvalidEmail() throws Exception {
        // Missing email
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(PASSWORD_PARAM, TEST_PASSWORD))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(REGISTRATION_ERROR_PARAM))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_USER_EMAIL_NULL))
                .andReturn();

        // Invalid email address
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(EMAIL_PARAM, "invalid")
                .param(PASSWORD_PARAM, TEST_PASSWORD))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(REGISTRATION_ERROR_PARAM))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_INVALID_EMAIL))
                .andReturn();
    }

    @Test
    public void testCreateUserInvalidPassword() throws Exception {
        // Invalid password
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(PASSWORD_PARAM, "12345"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(REGISTRATION_ERROR_PARAM))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_USER_PW_SIZE))
                .andReturn();

        // Password missing
        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(EMAIL_PARAM, TEST_EMAIL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(REGISTRATION_ERROR_PARAM))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_USER_PW_NULL))
                .andReturn();
    }



    @WithMockUser
    @Test
    public void testUpdateUser() throws Exception {
        // Updates both email and password
        mockMvc.perform(MockMvcRequestBuilders.put(HARDCODED_USER_UPDATE_ENDPOINT)
                .param(EMAIL_PARAM, TEST_EMAIL_UPDATE)
                .param(PASSWORD_PARAM, TEST_PASSWORD_UPDATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath(EMAIL_PARAM).value(TEST_EMAIL_UPDATE))
                .andDo(print())
                .andReturn();

        // Updates only password
        mockMvc.perform(MockMvcRequestBuilders.put(HARDCODED_USER_UPDATE_ENDPOINT)
                .param(PASSWORD_PARAM, TEST_PASSWORD_UPDATE))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @WithMockUser
    @Test
    public void testUpdateUserInvalidEmail() throws Exception {
        // no params
        mockMvc.perform(MockMvcRequestBuilders.put(HARDCODED_USER_UPDATE_ENDPOINT))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(UPDATE_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(INVALID_UPDATE_INFORMATION))
                .andReturn();

        // Invalid email
        mockMvc.perform(MockMvcRequestBuilders.put(HARDCODED_USER_UPDATE_ENDPOINT)
                .param(EMAIL_PARAM, "invalid"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(UPDATE_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_INVALID_EMAIL))
                .andReturn();


        // Invalid password
        mockMvc.perform(MockMvcRequestBuilders.put(HARDCODED_USER_UPDATE_ENDPOINT)
                .param(PASSWORD_PARAM, "12345"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(UPDATE_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_USER_PW_SIZE))
                .andReturn();
    }

    @WithMockUser
    @Test
    public void testUpdateUserInvalidPassword() throws Exception {
        // Invalid password
        mockMvc.perform(MockMvcRequestBuilders.put(HARDCODED_USER_UPDATE_ENDPOINT)
                .param(PASSWORD_PARAM, "12345"))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(UPDATE_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_USER_PW_SIZE))
                .andReturn();

        // Empty password
        mockMvc.perform(MockMvcRequestBuilders.put(HARDCODED_USER_UPDATE_ENDPOINT)
                .param(PASSWORD_PARAM, ""))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(UPDATE_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_USER_PW_SIZE))
                .andReturn();
    }

    @Test
    public void testNotAuthorizedGetUserInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(HARDCODED_USER_GET_INFO_ENDPOINT).with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNotAuthorizedUpdateUserInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(HARDCODED_USER_UPDATE_ENDPOINT).with(anonymous()))
                .andExpect(status().isUnauthorized());

    }


    public MvcResult performUserRegistration(final String email, final String password) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(EMAIL_PARAM, email)
                .param(PASSWORD_PARAM, password))
                .andDo(print())
                .andReturn();
    }

}