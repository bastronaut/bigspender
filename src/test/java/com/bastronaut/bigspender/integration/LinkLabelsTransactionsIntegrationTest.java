package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LabelRepository;
import com.bastronaut.bigspender.repositories.TransactionRepository;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.MockJsonReader;
import com.bastronaut.bigspender.utils.SampleData;
import org.hamcrest.Matchers;
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

import static com.bastronaut.bigspender.utils.SampleData.HEADER_ENCODED_USERONE;
import static com.bastronaut.bigspender.utils.TestConstants.LABELSID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.LABELS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.LABEL_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_LABELS;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isOneOf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // required to reset state after test
@AutoConfigureMockMvc
@ContextConfiguration
public class LinkLabelsTransactionsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LabelRepository labelRepository;

    private MockMvc mockMvc;

    final private String headerEncodedUserOne = SampleData.HEADER_ENCODED_USERONE;

    private SampleData sampleData;
    private User testUserOne;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        sampleData = new SampleData();
        testUserOne =  sampleData.getTestUserOne();
        // Setup initial users for various user related tests
        testUserOne = userRepository.save(testUserOne);
    }

    /**
     * Tests:
     * - Whether labels are properly linked to a transaction
     * - Whether transactions that don't exist for the user are ignored
     * - Whether labels that don't exist for the user are ignored
     * - The response structure of the api
     * - Whether the labels are properly linked upon retrieving a transaction
     * @throws Exception
     */
    @Transactional
    @Test
    public void testLinkLabelsToTransactions() throws Exception {
        // Setup test transactions
        final Transaction transactionOne =  transactionRepository.save(sampleData.t1);
        final Transaction transactionTwo =  transactionRepository.save(sampleData.t2);
        final Transaction transactionThree =  transactionRepository.save(sampleData.t3);
        final String transactionOneId = String.valueOf(transactionOne.getId());
        final String transactionTwoId = String.valueOf(transactionTwo.getId());
        final String transactionThreeId = String.valueOf(transactionThree.getId());

        // Setup test labels
        final Label labelOne = labelRepository.save(sampleData.getLabelOne());
        final Label labelTwo = labelRepository.save(sampleData.getLabelTwo());
        final Label labelThree = labelRepository.save(sampleData.getLabelThree());
        final String labelOneId = String.valueOf(labelOne.getId());
        final String labelTwoId = String.valueOf(labelTwo.getId());
        final String labelThreeId = String.valueOf(labelThree.getId());

        final String testLinkLabelsToTransactionJson = MockJsonReader
                .readMockJsonAsString("testLinkLabelsToTransaction.json")
                .replaceAll("REPLACE-TXID-1", String.valueOf(transactionOne.getId()))
                .replaceAll("REPLACE-TXID-2", String.valueOf(transactionTwo.getId()))
                .replaceAll("REPLACE-TXID-3", String.valueOf(transactionThree.getId()))
                .replaceAll("REPLACE-LABELID-1", String.valueOf(labelOne.getId()))
                .replaceAll("REPLACE-LABELID-2", String.valueOf(labelTwo.getId()))
                .replaceAll("REPLACE-LABELID-3", String.valueOf(labelThree.getId()));

        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACTION_LABELS)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testLinkLabelsToTransactionJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links", hasSize(3)))
                .andExpect(jsonPath("$.links[0].transactionId").value(transactionOneId))
                .andExpect(jsonPath("$.links[0].labelIds", hasSize(3)))
                .andExpect(jsonPath("$.links[0].labelIds[0]").value(labelOneId))
                .andExpect(jsonPath("$.links[0].labelIds[1]").value(labelTwoId))
                .andExpect(jsonPath("$.links[0].labelIds[2]").value(labelThreeId))
                .andExpect(jsonPath("$.links[1].transactionId").value(transactionTwoId))
                .andExpect(jsonPath("$.links[1].labelIds", hasSize(2)))
                .andExpect(jsonPath("$.links[1].labelIds[0]").value(labelTwoId))
                .andExpect(jsonPath("$.links[1].labelIds[1]").value(labelThreeId))
                .andExpect(jsonPath("$.links[2].transactionId").value(transactionThreeId))
                .andExpect(jsonPath("$.links[2].labelIds", hasSize(1)))
                .andExpect(jsonPath("$.links[2].labelIds[0]").value(String.valueOf(labelThreeId)));


        // Verify that the transaction has labels attached to it
        final String getTransactionEndpoint = TRANSACTION_ENDPOINT
                .replace(TRANSACTIONID_PARAM_REPLACE, transactionOneId);

        // As labels are stored as a set we can't guarantee the order of the label ids. Have to cast to int because
        // otherwise it would match against: one of {<5L>, <6L>, <7L>}""
        mockMvc.perform(MockMvcRequestBuilders.get(getTransactionEndpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_USERONE))
                .andDo(print())
                .andExpect(jsonPath("$.labels", hasSize(3)))
                .andExpect(jsonPath("$.labels[0].id", isOneOf((int)labelOne.getId(),
                        (int)labelTwo.getId(), (int)labelThree.getId())))

                .andExpect(jsonPath("$.labels[1].id", isOneOf((int)labelOne.getId(),
                        (int)labelTwo.getId(), (int)labelThree.getId())))

                .andExpect(jsonPath("$.labels[2].id", isOneOf((int)labelOne.getId(),
                        (int)labelTwo.getId(), (int)labelThree.getId())));
    }

    @Transactional
    @Test
    public void testUnlinkLabelsFromTransactions() throws Exception {
        // Setup test transactions
        Transaction transactionOne = sampleData.t1;
        Transaction transactionTwo = sampleData.t2;
        Transaction transactionThree = sampleData.t3;

        // Setup test labels
        final Label labelOne = labelRepository.save(sampleData.getLabelOne());
        final Label labelTwo = labelRepository.save(sampleData.getLabelTwo());
        final Label labelThree = labelRepository.save(sampleData.getLabelThree());
        final Label labelFour = labelRepository.save(sampleData.getLabelFour());

        transactionOne.addLabel(labelOne);
        transactionOne.addLabel(labelTwo);
        transactionOne.addLabel(labelThree);
        transactionOne.addLabel(labelFour); // Added but not part of unlink json

        transactionTwo.addLabel(labelTwo);
        transactionTwo.addLabel(labelThree);

        transactionThree.addLabel(labelThree);

        transactionOne = transactionRepository.save(transactionOne);
        transactionTwo = transactionRepository.save(transactionTwo);
        transactionThree = transactionRepository.save(transactionThree);
        final String labelOneId = String.valueOf(labelOne.getId());
        final String labelTwoId = String.valueOf(labelTwo.getId());
        final String labelThreeId = String.valueOf(labelThree.getId());

        final String transactionOneId = String.valueOf(transactionOne.getId());
        final String transactionTwoId = String.valueOf(transactionTwo.getId());
        final String transactionThreeId = String.valueOf(transactionThree.getId());

        final String testUnlinkLabelsToTransactionsJson = MockJsonReader
                .readMockJsonAsString("testUnlinkLabelsFromTransaction.json")
                .replaceAll("REPLACE-TXID-1", String.valueOf(transactionOne.getId()))
                .replaceAll("REPLACE-TXID-2", String.valueOf(transactionTwo.getId()))
                .replaceAll("REPLACE-TXID-3", String.valueOf(transactionThree.getId()))
                .replaceAll("REPLACE-LABELID-1", String.valueOf(labelOne.getId()))
                .replaceAll("REPLACE-LABELID-2", String.valueOf(labelTwo.getId()))
                .replaceAll("REPLACE-LABELID-3", String.valueOf(labelThree.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete(TRANSACTION_LABELS)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUnlinkLabelsToTransactionsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links", hasSize(3)))
                .andExpect(jsonPath("$.links[0].transactionId").value(transactionOneId))
                .andExpect(jsonPath("$.links[0].labelIds", hasSize(3)))
                .andExpect(jsonPath("$.links[0].labelIds[0]").value(labelOneId))
                .andExpect(jsonPath("$.links[0].labelIds[1]").value(labelTwoId))
                .andExpect(jsonPath("$.links[0].labelIds[2]").value(labelThreeId))
                .andExpect(jsonPath("$.links[1].transactionId").value(transactionTwoId))
                .andExpect(jsonPath("$.links[1].labelIds", hasSize(2)))
                .andExpect(jsonPath("$.links[1].labelIds[0]").value(labelTwoId))
                .andExpect(jsonPath("$.links[1].labelIds[1]").value(labelThreeId))
                .andExpect(jsonPath("$.links[2].transactionId").value(transactionThreeId))
                .andExpect(jsonPath("$.links[2].labelIds", hasSize(1)))
                .andExpect(jsonPath("$.links[2].labelIds[0]").value(String.valueOf(labelThreeId)));

        // Verify that the transaction does not the removed labels attached to it, only the one kept intact
        final String getTransactionEndpoint = TRANSACTION_ENDPOINT
                .replace(TRANSACTIONID_PARAM_REPLACE, transactionOneId);

        mockMvc.perform(MockMvcRequestBuilders.get(getTransactionEndpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_USERONE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.labels", hasSize(1)))
                .andExpect(jsonPath("$.labels[0].id").value(labelFour.getId()));

    }




}
