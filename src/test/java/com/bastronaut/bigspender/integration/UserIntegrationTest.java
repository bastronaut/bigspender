package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.SampleData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.TestConstants.EMAIL_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_INVALID_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_REGISTRATION_NOT_ALLOWED;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_USER_PW_SIZE;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DETAILS_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_MESSAGE_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.PASSWORD_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.REGISTRATION_ERROR_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.UPDATE_ERROR_MSG;
import static com.bastronaut.bigspender.utils.TestConstants.USERID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.USER_ENDPOINT;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // required to reset state after test
@AutoConfigureMockMvc
@ContextConfiguration
public class UserIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private SampleData sampleData = new SampleData();

    private MockMvc mockMvc;

    final private String userpw = TEST_EMAIL + ":" + TEST_PASSWORD;
    final private String headerEncoded = "Basic " + (Base64.getEncoder().encodeToString(userpw.getBytes()));
    private String userId;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup initial user for various user related tests
        final User testUser = sampleData.getTestUserOne();
        userRepository.save(testUser);

        // Resources are often queried by the user id (in endpoints), we must find the exact user id to set correct resource paths
        final Optional<User> optionalUser = userRepository.findByEmail(TEST_EMAIL);
        userId = String.valueOf(optionalUser.get().getId());
    }

    @Test
    public void testUpdateUser() throws Exception {

        // Update user, auth headers are required to inject the User argument into the controller
        mockMvc.perform(MockMvcRequestBuilders.put(USER_ENDPOINT.replace(USERID_PARAM_REPLACE, userId))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param(EMAIL_PARAM, TEST_EMAIL_UPDATE))
                .andDo(print())
                .andExpect(jsonPath(EMAIL_PARAM).value(TEST_EMAIL_UPDATE)).andReturn();
    }

    @Test
    public void testUpdateUserInvalid() throws Exception {

        // Invalid email address
        mockMvc.perform(MockMvcRequestBuilders.put(USER_ENDPOINT.replace(USERID_PARAM_REPLACE, userId))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param(EMAIL_PARAM, "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(UPDATE_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_INVALID_EMAIL))
                .andDo(print())
                .andReturn();

        // Invalid password
        mockMvc.perform(MockMvcRequestBuilders.put(USER_ENDPOINT.replace(USERID_PARAM_REPLACE, userId))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param(PASSWORD_PARAM, "12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(UPDATE_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_USER_PW_SIZE))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void testRegisterUserExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(PASSWORD_PARAM, TEST_PASSWORD))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(REGISTRATION_ERROR_PARAM))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value("User already exists: " + TEST_EMAIL))
                .andDo(print())
                .andReturn();
    }

    /**
     * Test performing a get operation on a user with incorrect credentaisl in the uathorization headerd
     * @throws Exception
     */
    @Test
    public void testGetUserWrongPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                .header(HttpHeaders.AUTHORIZATION, SampleData.HEADER_ENCODED_USERONEWRONGPW))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void createUserAlreadyLoggedInShouldFail() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
//                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param(EMAIL_PARAM, "test2@email.com")
                .param(PASSWORD_PARAM, "testpw123"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(REGISTRATION_ERROR_PARAM))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_REGISTRATION_NOT_ALLOWED));

    }

}
