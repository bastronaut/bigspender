package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.LoginAttempt;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LoginAttemptRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.services.LoginAttemptService;
import com.bastronaut.bigspender.utils.SampleData;
import com.bastronaut.bigspender.utils.TestUtils;
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

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.ApplicationConstants.LOGIN_ENDPOINT;
import static com.bastronaut.bigspender.utils.SampleData.HEADER_ENCODED_USERONE;
import static com.bastronaut.bigspender.utils.TestConstants.LOGINHEADER_ENCODED;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.USERS_ENDPOINT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // required to reset state after test
@AutoConfigureMockMvc
@ContextConfiguration
public class LoginAttemptIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    private MockMvc mockMvc;

    private SampleData sampleData = new SampleData();

    private String usernamePassword;
    private String incorrectLoginHeader;
    private User testuser;

    @Before
    public void setUp() throws Exception {
        mockMvc = TestUtils.setupMockMvc(context);

        // Setup initial user for various user related tests
        testuser = sampleData.getTestUserOne();
        userRepository.save(testuser);
    }


    @Test
    @Transactional
    public void testUserNotBlockedByDefault() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_USERONE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testTooManyLoginAttemptsBlocksUser() throws Exception {

        usernamePassword = testuser.getEmail() + ":" + "incorrectpassword";
        incorrectLoginHeader = "Basic " + (Base64.getEncoder().encodeToString(usernamePassword.getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, incorrectLoginHeader))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // empty password
        usernamePassword = testuser.getEmail() + ":" + "";
        incorrectLoginHeader = "Basic " + (Base64.getEncoder().encodeToString(usernamePassword.getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, incorrectLoginHeader))
                .andDo(print())
                .andExpect(status().isUnauthorized());



        usernamePassword = testuser.getEmail() + ":" + "FAKEPASSWORD";
        incorrectLoginHeader = "Basic " + (Base64.getEncoder().encodeToString(usernamePassword.getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, incorrectLoginHeader))
                .andDo(print())
                .andExpect(status().isUnauthorized());


        // 4th attempt with bad credentials, login blocked
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, incorrectLoginHeader))
                .andDo(print())
                .andExpect(status().isForbidden());

        // 5th attempt with correct credentials, verify account is locked
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_USERONE))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    /**
     * The account should automatically unlock after MINUTES_ACCOUNT_LOCKED time. Verifies that, when the
     * time since the last login attempt was a long time ago, that the account is unlocked
     */
    @Test
    @Transactional
    public void testTooManyLoginAttemptsBlockRemovedAfterTimeout() throws Exception {

        usernamePassword = testuser.getEmail() + ":" + "incorrectpassword";
        incorrectLoginHeader = "Basic " + (Base64.getEncoder().encodeToString(usernamePassword.getBytes()));

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, incorrectLoginHeader))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, incorrectLoginHeader))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, incorrectLoginHeader))
                .andExpect(status().isUnauthorized());

        // 4th attempt, with good credentials, login blocked
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_USERONE))
                .andExpect(status().isForbidden());

        final LocalDateTime longTimeAgo = LocalDateTime.of(1989, 01, 01, 1, 1);
        final LoginAttempt attempts = loginAttemptRepository.findById(testuser.getEmail()).get();
        attempts.setMostRecentAttempt(longTimeAgo);

        // 1st attempt since a longTimeAgo, should allow login again
        mockMvc.perform(MockMvcRequestBuilders.get(LOGIN_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_USERONE))
                .andExpect(status().isOk());

    }
}
