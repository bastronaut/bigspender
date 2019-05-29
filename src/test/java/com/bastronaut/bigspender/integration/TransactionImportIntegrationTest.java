package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.controllers.UserController;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.SampleData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_IMPORT_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.USERID_PARAM_REPLACE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // required to reset state after test
@AutoConfigureMockMvc
@ContextConfiguration
public class TransactionImportIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    final private String userpw = TEST_EMAIL + ":" + TEST_PASSWORD;
    final private String headerEncoded = "Basic " + (Base64.getEncoder().encodeToString(userpw.getBytes()));
    private String userid;
    private List<Transaction> transactions;


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
    public void testImportTransactions() throws Exception {

        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        final FileInputStream input = new FileInputStream(sampleFile);

        final MockMultipartFile sampleCSV = new MockMultipartFile("file", sampleFile.getName(),
                "multipart/form-data", input);

        mockMvc.perform(MockMvcRequestBuilders.multipart(TRANSACTION_IMPORT_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
                .file(sampleCSV)
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param("bankName", "ing"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[:1].date").value("2019-04-01"))
                .andExpect(jsonPath("$.transactions[:1].time").value("22:39:00"))
                .andExpect(jsonPath("$.transactions[:1].accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("$.transactions[:1].code").value("GT"))
                .andExpect(jsonPath("$.transactions[:1].type").value("AF"))
                .andExpect(jsonPath("$.transactions[6:].receivingAccountNumber").value("NL20INGB0001987654"))
                .andExpect(jsonPath("$.transactions[6:].amount").value(1980))
                .andExpect(jsonPath("$.transactions[6:].mutationType").value("DIVERSEN"))
                .andExpect(jsonPath("$.user.email").value("test@email.com"))
                .andExpect(jsonPath("$.user.name").value("tester"))
                .andExpect(jsonPath("$.transactions[6:].statement").value("Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019"))
                .andExpect(jsonPath("$.transactions[6:].day").value("SUNDAY"));
    }
}
