package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LabelRepository;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.MockJsonReader;
import com.bastronaut.bigspender.utils.SampleData;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.bastronaut.bigspender.utils.TestConstants.DEFAULT_LABELCOLOR;
import static com.bastronaut.bigspender.utils.TestConstants.DEFAULT_LABELNAME;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_LABEL_EMPTY;
import static com.bastronaut.bigspender.utils.TestConstants.ERRORMSG_LABEL_NAME_EMPTY;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DETAILS_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_MESSAGE_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.LABELS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.LABELS_PER_TRANSACTION_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.LABEL_ERROR_MSG;
import static com.bastronaut.bigspender.utils.TestConstants.LABEL_PER_TRANSACTION_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONID_PARAM_REPLACE;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // required to reset state after test
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

    private SampleData sampleData;
    private User testUserOne;

    @Autowired
    EntityManager entityManager;
    

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userRepository.deleteAllInBatch();
        transactionRepository.deleteAllInBatch();
        labelRepository.deleteAllInBatch();
        // Clear all entity caches
        entityManager.getEntityManagerFactory().getCache().evictAll();

        sampleData = new SampleData();
        testUserOne =  sampleData.getTestUserOne();
        // Setup initial users for various user related tests
        testUserOne = userRepository.save(testUserOne);
    }


    @Transactional
    @Test
    public void testCreateLabels() throws Exception {
        final String endpoint = LABELS_ENDPOINT;

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
                .andExpect(jsonPath("$.labels[2].color").value(DEFAULT_LABELCOLOR))
                .andExpect(jsonPath("$.labels[3].name").value("subscriptions"))
                .andExpect(jsonPath("$.labels[3].color").value("#123EFA"));
    }

    @Transactional
    @Test
    public void testGetLabels() throws Exception {
        // Setup labels to update
        Label labelOne = sampleData.getLabelOne();
        Label labelTwo = sampleData.getLabelTwo();
        Label labelThree = sampleData.getLabelThree();
        labelOne = labelRepository.save(labelOne);
        labelTwo = labelRepository.save(labelTwo);
        labelThree = labelRepository.save(labelThree);

        mockMvc.perform(MockMvcRequestBuilders.get(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(3)))
                .andExpect(jsonPath("$.labels[0].name").value(labelOne.getName()))
                .andExpect(jsonPath("$.labels[0].color").value(labelOne.getColor()))
                .andExpect(jsonPath("$.labels[0].id").value(labelOne.getId()))
                .andExpect(jsonPath("$.labels[1].name").value(labelTwo.getName()))
                .andExpect(jsonPath("$.labels[1].color").value(labelTwo.getColor()))
                .andExpect(jsonPath("$.labels[1].id").value(labelTwo.getId()))
                .andExpect(jsonPath("$.labels[2].name").value(labelThree.getName()))
                .andExpect(jsonPath("$.labels[2].color").value(labelThree.getColor()))
                .andExpect(jsonPath("$.labels[2].id").value(labelThree.getId()))
                .andReturn();
    }

    /**
     * Creates and saves three labels, but assigns two a transaction. Test retrieving thesee two labels
     * Example reply:
     * {
     *   transactionid: 1,
     *   labels: [
     *      {"name": "subscriptions", "color": "#123EFA", id: 1},
     *      {"name": "groceries", "color": "#EEE", id: 2}
     *   ]
     *  }
     *
     * @throws Exception
     */
    @Transactional
    @Test
    public void testGetLabelsForTransactionAssignedToLabels() throws  Exception {
        // Start setting up labels and assigning to transaction
        Label labelOne = sampleData.getLabelOne();
        Label labelTwo = sampleData.getLabelTwo(); // purposely
        Label labelThree = sampleData.getLabelThree();
        final List<Label> testLabels = new ArrayList<>();
        testLabels.add(labelOne);
        testLabels.add(labelTwo);
        testLabels.add(labelThree);

        final List<Label> savedLabels = labelRepository.saveAll(testLabels);

        final Transaction t1 = sampleData.t1;
        // Purposely only add two out of three labels
        final Set<Label> labelsToRetrieve = new HashSet<>();
        labelsToRetrieve.add(labelOne);
        labelsToRetrieve.add(labelThree);
        t1.setLabels(labelsToRetrieve);

        final Transaction t1Saved = transactionRepository.save(t1);

        final String endpoint = LABELS_PER_TRANSACTION_ENDPOINT.replace(
                TRANSACTIONID_PARAM_REPLACE, String.valueOf(t1Saved.getId()));
        // End setup


        // Potential problem: as Labels are stored as a Set, may not be returned in order. May have to return
        // and do processing that way
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(2)))
                .andExpect(jsonPath("$.labels[0].name").value(labelOne.getName()))
                .andExpect(jsonPath("$.labels[0].id").value(labelOne.getId()))
                .andExpect(jsonPath("$.labels[0].color").value(labelOne.getColor()))
                .andExpect(jsonPath("$.labels[1].name").value(labelThree.getName()))
                .andExpect(jsonPath("$.labels[1].id").value(labelThree.getId()))
                .andExpect(jsonPath("$.labels[1].color").value(labelThree.getColor()))
                .andExpect(jsonPath("$.transactionId").value(t1Saved.getId()))
                .andReturn();

    }

    @Transactional
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

    /**
     * Tests 5 cases for updating a label:
     * - Updating a name and color for an existing label
     * - Updating a name for an existing label
     * - Updating a color for an existing label
     * - Creating a new label from an update (PUT) request with name and color, for nonexistant id
     * - Creating a new label from an update (PUT) request with a color, for nonexistant id
     * @throws Exception
     */
    @Transactional
    @Test
    public void testUpdateLabels() throws Exception {

        // Setup labels to update
        Label labelOne = sampleData.getLabelOne();
        Label labelTwo = sampleData.getLabelTwo();
        Label labelThree = sampleData.getLabelThree();
        labelOne = labelRepository.save(labelOne);
        labelTwo = labelRepository.save(labelTwo);
        labelThree = labelRepository.save(labelThree);

        final String testUpdateLabelsJson = MockJsonReader
                .readMockJsonAsString("testUpdateLabels.json")
                .replace("\"{REPLACE-1}\"", String.valueOf(labelOne.getId()))
                .replace("\"{REPLACE-2}\"", String.valueOf(labelTwo.getId()))
                .replace("\"{REPLACE-3}\"", String.valueOf(labelThree.getId()));

        mockMvc.perform(MockMvcRequestBuilders.put(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUpdateLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(3)))
                .andExpect(jsonPath("$.labels[0].name").value("groceries-new"))
                .andExpect(jsonPath("$.labels[0].color").value("#17A2B8"))
                .andExpect(jsonPath("$.labels[1].name").value("insurance-new"))
                .andExpect(jsonPath("$.labels[1].color").value("#1124AD"))
                .andExpect(jsonPath("$.labels[2].name").value("non-existing-label"))
                .andExpect(jsonPath("$.labels[2].color").value("#FFC107"))
                .andReturn();

    }

    @Transactional
    @Test
    public void testDeleteLabel() throws Exception {

        // Setup labels to remove
        final Label labelOne = sampleData.getLabelOne();
        final Label labelTwo = sampleData.getLabelTwo();

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

    @Transactional
    @Test
    public void testDeleteLabels() throws Exception {

        // Setup labels to remove
        final Label labelOne = sampleData.getLabelOne();
        final Label labelTwo = sampleData.getLabelTwo();
        labelRepository.save(labelOne);
        labelRepository.save(labelTwo);

        final String testDeleteLabelsJson = MockJsonReader.readMockJsonAsString("testDeleteLabels.json")
                .replace("\"{REPLACE-1}\"", String.valueOf(labelOne.getId()))
                .replace("\"{REPLACE-2}\"", String.valueOf(labelTwo.getId()));


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

        final Transaction testTransactionOne = sampleData.t1;
        transactionRepository.save(testTransactionOne );

        final Label labelOne = sampleData.getLabelOne();
        labelRepository.save(labelOne);


        testTransactionOne.addLabel(labelOne);
        transactionRepository.save(testTransactionOne);

        // Verify the label is attached to the transaction
        final Optional<Transaction> verifyTransactionOne = transactionRepository
                .findByIdAndUser(testTransactionOne.getId(), testUserOne);

        final Set<Label> verifyLabelsOne = verifyTransactionOne.get().getLabels();
        final Label firstLabelResult = verifyLabelsOne.iterator().next();
        assert(verifyLabelsOne.size() == 1);
        assertEquals(firstLabelResult.getId(), labelOne.getId());

        // We ensure that the ID to remove matches the ID from testTransactionOne
        final String testDeleteLabelsJson = MockJsonReader
                .readMockJsonAsString("testDeleteLabelUnassignsFromTransaction.json")
                .replace("\"{REPLACE}\"", String.valueOf(labelOne.getId()));

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

        final Set<Label> verifyLabelsRemoved = verifyTransactionOneRemoved.get().getLabels();
        assert(verifyLabelsRemoved.isEmpty());
    }



    @Transactional
    @Test
    public void testDeleteLabelUnassignsFromTransactionLeavesRemainingLabelsIntact() throws Exception {
        final Transaction testTransactionOne = sampleData.t1;
        final Transaction testTransactionTwo = sampleData.t2;

        final List<Transaction> testTransactions = new ArrayList<>();
        testTransactions.add(testTransactionOne);
        testTransactions.add(testTransactionTwo);

        transactionRepository.saveAll(testTransactions);

        final Label labelOne = sampleData.getLabelOne();
        final Label labelTwo = sampleData.getLabelTwo();

        final List<Label> testLabels = new ArrayList<>();
        testLabels.add(labelOne);
        testLabels.add(labelTwo);

        labelRepository.saveAll(testLabels);

        // Test a transaction with one label attached
        testTransactionOne.addLabel(labelOne);

        // Test a transaction with multiple labels attached that only one is removed
        testTransactionTwo.addLabel(labelOne);
        testTransactionTwo.addLabel(labelTwo);

        transactionRepository.saveAll(testTransactions);
        //**  End setup test entities ** //

        // We ensure that the ID to remove matches the ID from testTransactionOne
        final String testDeleteLabelsJson = MockJsonReader
                .readMockJsonAsString("testDeleteLabelUnassignsFromTransaction.json")
                .replace("\"{REPLACE}\"", String.valueOf(labelOne.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete(LABELS_ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testDeleteLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(1)))
                .andExpect(jsonPath("$.labels[0].id").value(labelOne.getId()));

        // Verify the label is removed from the transaction
        final Optional<Transaction> verifyTransactionOne = transactionRepository
                .findByIdAndUser(testTransactionOne.getId(), testUserOne);

        final Set<Label> verifyLabelsOne = verifyTransactionOne.get().getLabels();
        assert(verifyLabelsOne.isEmpty());

        // Verify the labels is removed, but the other label is still present
        final Optional<Transaction> verifyTransactionTwo = transactionRepository
                .findByIdAndUser(testTransactionTwo.getId(), testUserOne);
        final Set<Label> verifyLabelsTwo = verifyTransactionTwo.get().getLabels();
        assert(verifyLabelsTwo.size() == 1);
        assert(verifyLabelsTwo.iterator().next().getId() == labelTwo.getId());

    }


//    @Test
//    public void testAddLabelToTransaction() throws Exception {
//        assert(false);
//    }
//
//    @Test
//    public void testAddNewLabelToTransaction() throws Exception {
//        assert(false);
//    }
//
//    @Test
//    public void testRemoveLabelFromTransaction() throws Exception {
//        assert(false);
//    }
//
//    @Test
//    public void testAddLabelToTransactions() throws Exception {
//        assert(false);
//    }
//
//    @Test
//    public void testRemoveAllLabelsFromTransaction() throws Exception {
//        assert(false);
//    }





}
