package com.bastronaut.bigspender.integration;


import com.bastronaut.bigspender.controllers.UserController;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.SampleData;
import org.junit.After;
import org.junit.Before;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.TestConstants.EMAIL_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DETAILS_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_MESSAGE_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static com.bastronaut.bigspender.utils.TestConstants.NAME_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.PASSWORD_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_EMAIL_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_FIRSTNAME;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_FIRSTNAME_UPDATE;
import static com.bastronaut.bigspender.utils.TestConstants.TEST_PASSWORD;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_IMPORT_ENDPOINT;
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
public class IntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;

    final String userpw = TEST_EMAIL + ":" + TEST_PASSWORD;
    final String headerEncoded = "Basic " + (Base64.getEncoder().encodeToString(userpw.getBytes()));
    String userid;
    List<Transaction> transactions;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup initial user for various user related tests
        final User testuser = SampleData.getTestUser();
        userRepository.save(testuser);

        // Setup sample transactions for validation
        this.transactions = SampleData.getTransactions();
        transactionRepository.saveAll(transactions);

        // Resources are often queried by the user id (in endpoints), we must find the exact user id to set correct resource paths
        final Optional<User> optionalUser = userRepository.findByEmail(TEST_EMAIL);
        userid = String.valueOf(optionalUser.get().getId());
    }



    @Test
    public void testUpdateUser() throws Exception {

        // Update user, auth headers are required to inject the User argument into the controller
        mockMvc.perform(MockMvcRequestBuilders.put(USERS_UPDATE_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
        .header(HttpHeaders.AUTHORIZATION, headerEncoded)
        .param(EMAIL_PARAM, TEST_EMAIL_UPDATE)
        .param(NAME_PARAM, TEST_FIRSTNAME_UPDATE))
                .andDo(print())
                .andExpect(jsonPath(EMAIL_PARAM).value(TEST_EMAIL_UPDATE))
                .andExpect(jsonPath(NAME_PARAM).value(TEST_FIRSTNAME_UPDATE)).andReturn();
    }

    @Test
    public void testRegisterUserExists() throws Exception {

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
                .param(NAME_PARAM, TEST_FIRSTNAME)
                .param(EMAIL_PARAM, TEST_EMAIL)
                .param(PASSWORD_PARAM, TEST_PASSWORD))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value("Registration error"))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value("User already exists: " + TEST_EMAIL))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void testImportTransactions() throws Exception {

        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        final FileInputStream input = new FileInputStream(sampleFile);

        final MockMultipartFile sampleCSV = new MockMultipartFile("file", sampleFile.getName(),
                "multipart/form-data", input);

        mockMvc.perform(MockMvcRequestBuilders.multipart(TRANSACTION_IMPORT_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
                .file(sampleCSV)
                .header(HttpHeaders.AUTHORIZATION, headerEncoded))
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

    @Test
    public void testAddTransactionForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded)
                .param("date", "2019-04-07")
                .param("time", "07:25:00")
                .param("name" , "Test transaction")
                .param("accountNumber" , "NL20INGB0004567891")
                .param("receivingAccountNumber" , "NL20INGB0001987654")
                .param("code" , "BA")
                .param("type" , "BIJ")
                .param("amount" , "1980")
                .param("mutationType" , "DIVERSEN")
                .param("statement" , "Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019")
                .param("day" , "SUNDAY"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2019-04-07"))
                .andExpect(jsonPath("$.time").value("07:25:00"))
                .andExpect(jsonPath("$.name").value("Test transaction"))
                .andExpect(jsonPath("$.accountNumber").value("NL20INGB0004567891"))
                .andExpect(jsonPath("$.receivingAccountNumber").value("NL20INGB0001987654"))
                .andExpect(jsonPath("$.type").value("BIJ"))
                .andExpect(jsonPath("$.amount").value("1980"))
                .andExpect(jsonPath("$.day").value("SUNDAY"));

    }

    @Test
    public void testGetTransactionsForUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(TRANSACTIONS_ENDPOINT.replace(USERID_PARAM_REPLACE, userid))
                .header(HttpHeaders.AUTHORIZATION, headerEncoded))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("$.[1].accountNumber").value("NL41INGB0006451386"))
                .andExpect(jsonPath("$.[2].accountNumber").value("NL20INGB0001234567"))
                .andExpect(jsonPath("$.[3].accountNumber").value("NL20INGB0001234567"))
                .andExpect(jsonPath("$.[4].accountNumber").value("NL20INGB0002345678"))
                .andExpect(jsonPath("$.[5].accountNumber").value("NL20INGB0003456789"))
                .andExpect(jsonPath("$.[6].accountNumber").value("NL20INGB0004567891"));
    }

    @Test
    public void testDeleteTransactionForUser() throws Exception {
        final Transaction firstTransaction = transactions.get(0);
        final String firstTransactionId = String.valueOf(firstTransaction.getId());
        final String deleteEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userid).replace(TRANSACTIONID_PARAM_REPLACE, firstTransactionId);

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isNoContent());

        // second run should be a 404 for nonexisting resource, so 2nd time deleting the resource
        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistingResource() throws Exception {
        final String nonExistingResource = "9999987654321";
        final String deleteEndpoint = TRANSACTION_ENDPOINT.replace(USERID_PARAM_REPLACE, userid).replace(TRANSACTIONID_PARAM_REPLACE, nonExistingResource);

        mockMvc.perform(MockMvcRequestBuilders.delete(deleteEndpoint)
                .param("id", "1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
