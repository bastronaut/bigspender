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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Base64;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.TestConstants.EMAIL_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DETAILS_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_MESSAGE_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.NAME_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.PASSWORD_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.USERID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.USERS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.USERS_UPDATE_ENDPOINT;
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

    private MockMvc mockMvc;

    final private String userpw = TEST_EMAIL + ":" + TEST_PASSWORD;
    final private String headerEncoded = "Basic " + (Base64.getEncoder().encodeToString(userpw.getBytes()));
    private String userid;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup initial user for various user related tests
        final User testuser = SampleData.getTestUser();
        userRepository.save(testuser);

        // Resources are often queried by the user id (in endpoints), we must find the exact user id to set correct resource paths
        final Optional<User> optionalUser = userRepository.findByEmail(TEST_EMAIL);
        userid = String.valueOf(optionalUser.get().getId());
    }

    @Test
    public void testUpdateUser() throws Exception {

        // Update user, auth headers are required to inject the User argument into the controller
        mockMvc.perform(MockMvcRequestBuilders.put(USERS_UPDATE_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param(EMAIL_PARAM, TEST_EMAIL_UPDATE))
                .andDo(print())
                .andExpect(jsonPath(EMAIL_PARAM).value(TEST_EMAIL_UPDATE)).andReturn();
    }

    @Test
    public void testRegisterUserExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(PASSWORD_PARAM, TEST_PASSWORD))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value("Registration error"))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value("User already exists: " + TEST_EMAIL))
                .andDo(print())
                .andReturn();
    }
}
