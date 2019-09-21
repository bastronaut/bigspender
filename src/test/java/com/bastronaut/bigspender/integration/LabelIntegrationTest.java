package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LabelRepository;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.MockJsonReader;
import com.bastronaut.bigspender.utils.SampleData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_INVALID_EMAIL;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_LABEL_EMPTY;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_LABEL_NAME_EMPTY;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DETAILS_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_MESSAGE_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.LABELS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.LABEL_ERROR_MSG;
import static com.bastronaut.bigspender.utils.TestConstants.REGISTRATION_ERROR_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.USERID_PARAM_REPLACE;
import static org.hamcrest.Matchers.hasSize;
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
public class LabelIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LabelRepository  labelRepository;

    private MockMvc mockMvc;

    final private String headerEncodedUserOne = SampleData.HEADER_ENCODED_USERONE;

    final private User testUserOne = SampleData.TESTUSERONE;
    private String userIdTestUserOne;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Setup initial users for various user related tests
        userRepository.save(testUserOne);
        userIdTestUserOne = String.valueOf(testUserOne.getId());
    }


    @Test
    public void testCreateLabels() throws Exception {
        final String endpoint = LABELS_ENDPOINT;
        final String defaultColor = "#000";

        final String createLabelsJson = MockJsonReader.readMockJsonAsString("testCreateLabels.json");

        mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(4)))
                .andExpect(jsonPath("$.labels[0].name").value("groceries"))
                .andExpect(jsonPath("$.labels[0].color").value("#EEE"))
                .andExpect(jsonPath("$.labels[1].name").value("insurance"))
                .andExpect(jsonPath("$.labels[1].color").value("#ABC"))
                .andExpect(jsonPath("$.labels[2].name").value("drinks"))
                .andExpect(jsonPath("$.labels[2].color").value(defaultColor))
                .andExpect(jsonPath("$.labels[3].name").value("subscriptions"))
                .andExpect(jsonPath("$.labels[3].color").value("#123EFA"));
    }

    @Test
    public void testCreateLabelsBadRequest() throws Exception {

        final String createLabelsMissingLabelsJson = MockJsonReader
                .readMockJsonAsString("testCreateLabelsBadRequestMissingLabelsJson.json");

        mockMvc.perform(MockMvcRequestBuilders.post(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createLabelsMissingLabelsJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(LABEL_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_LABEL_EMPTY))
                .andReturn();


        final String createLabelsMissingNameJson = MockJsonReader
                .readMockJsonAsString("testCreateLabelsBadRequestMissingNameJson.json");

        mockMvc.perform(MockMvcRequestBuilders.post(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createLabelsMissingNameJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(LABEL_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_LABEL_NAME_EMPTY))
                .andReturn();
    }

    @Test
    public void testUpdateLabels() throws Exception {

        // Setup labels to update
        final Label labelOne = new Label("groceries", testUserOne, "#111");
        final Label labelTwo = new Label("clothing", testUserOne, "#111");
        final Label labelThree = new Label("snacks", testUserOne, "#111");
        labelRepository.save(labelOne);
        labelRepository.save(labelTwo);
        labelRepository.save(labelThree);

        final String testUpdateLabelsJson = MockJsonReader
                .readMockJsonAsString("testUpdateLabels.json");

        mockMvc.perform(MockMvcRequestBuilders.put(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUpdateLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(4)))
                .andExpect(jsonPath("$.labels[0].name").value("groceries-new"))
                .andReturn();

    }

    @Transactional
    @Test
    public void testDeleteLabel() throws Exception {

        // Setup labels to remove
        final Label labelOne = new Label("groceries", testUserOne, "#111");
        final Label labelTwo = new Label("clothing", testUserOne, "#123");

        labelRepository.save(labelOne);
        labelRepository.save(labelTwo);

        final String testDeleteLabelJson = MockJsonReader.readMockJsonAsString("testDeleteLabel.json")
                .replace("\"{REPLACE}\"", String.valueOf(labelOne.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testDeleteLabelJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(1)))
                .andExpect(jsonPath("$.labels[0].id").value(labelOne.getId()));
    }

//    @Transactional // TODO No EntityManager with actual transaction available for current thread
    @Test
    public void testDeleteLabels() throws Exception {

        // Setup labels to remove
        final Label labelOne = new Label("groceries", testUserOne, "#111");
        final Label labelTwo = new Label("clothing", testUserOne, "#111");
        labelRepository.save(labelOne);
        labelRepository.save(labelTwo);

        final String testDeleteLabelsJson = MockJsonReader.readMockJsonAsString("testDeleteLabels.json");

        mockMvc.perform(MockMvcRequestBuilders.delete(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testDeleteLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(2)))
                .andExpect(jsonPath("$.labels[0].id").value(labelOne.getId()))
                .andExpect(jsonPath("$.labels[1].id").value(labelTwo.getId()));
    }

    /**
     * Upon deletion of a label, the reference on a Transaction should also be removed.
     * This test sets up a Transaction and a Label, and links these together. It then verifies that the link is
     * removed when the label is deleted
     * @throws Exception
     */
    @Transactional
    @Test
    public void testDeleteLabelUnassignsFromTransaction() throws Exception {

        final Transaction testTransactionOne = SampleData.t1;
        transactionRepository.save(testTransactionOne );

        final Label labelOne = new Label("groceries", testUserOne, "#111");
        labelRepository.save(labelOne);

        testTransactionOne.setLabels(Arrays.asList(labelOne));
        transactionRepository.save(testTransactionOne);

        // Verify the label is attached to the transaction
        final Optional<Transaction> verifyTransactionOne = transactionRepository
                .findByIdAndUser(testTransactionOne.getId(), testUserOne);

        final List<Label> verifyLabelsOne = verifyTransactionOne.get().getLabels();
        assert(verifyLabelsOne.size() == 1);
        assertEquals(verifyLabelsOne.get(0).getId(), labelOne.getId());

        // We ensure that the ID to remove matches the ID from testTransactionOne
        final String testDeleteLabelsJson = MockJsonReader
                .readMockJsonAsString("testDeleteLabelUnassignsFromTransaction.json.json")
                .replace("\"{REPLACE}\"", String.valueOf(testTransactionOne.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testDeleteLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(1)))
                .andExpect(jsonPath("$.labels[0].id").value(labelOne.getId()));

        // Verify the label is removed from the transaction
        final Optional<Transaction> verifyTransactionOneRemoved = transactionRepository
                .findByIdAndUser(testTransactionOne.getId(), testUserOne);

        final List<Label> verifyLabelsRemoved = verifyTransactionOneRemoved.get().getLabels();
        assert(verifyLabelsRemoved.isEmpty());
    }



    @Test
    public void testDeleteLabelUnassignsFromTransactionLeavesRemainingLabelsIntact() throws Exception {
        final Transaction testTransactionOne = SampleData.t1;
        final Transaction testTransactionTwo = SampleData.t2;

        List<Transaction> testTransactions = Arrays.asList(testTransactionOne, testTransactionTwo);

        transactionRepository.saveAll(testTransactions);

        final Label labelOne = new Label("groceries", testUserOne, "#111");
        final Label labelTwo = new Label("clothing", testUserOne, "#123");

        final List<Label> testLabels = Arrays.asList(labelOne, labelTwo);

        labelRepository.saveAll(testLabels);

        // Test a transaction with one label attached
        testTransactionOne.setLabels(Arrays.asList(labelOne));

        // Test a transaction with multiple labels attached that only one is removed
        testTransactionTwo.setLabels(Arrays.asList(labelOne, labelTwo));

        transactionRepository.saveAll(testTransactions);
        //**  End setup test entities ** //

        // We ensure that the ID to remove matches the ID from testTransactionOne
        final String testDeleteLabelsJson = MockJsonReader
                .readMockJsonAsString("testDeleteLabelUnassignsFromTransaction.json.json")
                .replace("\"{REPLACE}\"", String.valueOf(testTransactionOne.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testDeleteLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(1)))
                .andExpect(jsonPath("$.labels[0].id").value(testTransactionOne.getId()));

        // Verify the label is removed from the transaction
        final Optional<Transaction> verifyTransactionOne = transactionRepository
                .findByIdAndUser(testTransactionOne.getId(), testUserOne);

        final List<Label> verifyLabelsOne = verifyTransactionOne.get().getLabels();
        assert(verifyLabelsOne.isEmpty());

        // Verify the labels is removed, but the other label is still present
        final Optional<Transaction> verifyTransactionTwo = transactionRepository
                .findByIdAndUser(testTransactionTwo.getId(), testUserOne);
        final List<Label> verifyLabelsTwo = verifyTransactionTwo.get().getLabels();
        assert(verifyLabelsTwo.size() == 1);
        assert(verifyLabelsTwo.get(0).getId() == testTransactionTwo.getId());

    }


    @Test
    public void testAddLabelToTransaction() throws Exception {
        assert(false);
    }

    @Test
    public void testAddNewLabelToTransaction() throws Exception {
        assert(false);
    }

    @Test
    public void testRemoveLabelFromTransaction() throws Exception {
        assert(false);
    }

    @Test
    public void testAddLabelToTransactions() throws Exception {
        assert(false);
    }

    @Test
    public void testRemoveAllLabelsFromTransaction() throws Exception {
        assert(false);
    }





}
