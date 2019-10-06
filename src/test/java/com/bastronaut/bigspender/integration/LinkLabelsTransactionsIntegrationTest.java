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
    public void testAddLabelsToTransactions() throws Exception {
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

        final String testAddLabelsToTransactionsJson = MockJsonReader
                .readMockJsonAsString("testAddLabelsToTransaction.json")
                .replaceAll("REPLACE-TXID-1", String.valueOf(transactionOne.getId()))
                .replaceAll("REPLACE-TXID-2", String.valueOf(transactionTwo.getId()))
                .replaceAll("REPLACE-TXID-3", String.valueOf(transactionThree.getId()))
                .replaceAll("REPLACE-LABELID-1", String.valueOf(labelOne.getId()))
                .replaceAll("REPLACE-LABELID-2", String.valueOf(labelTwo.getId()))
                .replaceAll("REPLACE-LABELID-3", String.valueOf(labelThree.getId()));

        mockMvc.perform(MockMvcRequestBuilders.post(TRANSACTION_LABELS)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testAddLabelsToTransactionsJson))
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

        final String getTransactionEndpoint = TRANSACTION_ENDPOINT
                .replace(TRANSACTIONID_PARAM_REPLACE, transactionOneId);

        // As labels are stored as a set we can't guarantee the order of the label ids
        mockMvc.perform(MockMvcRequestBuilders.get(getTransactionEndpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_USERONE))
                .andDo(print())
                .andExpect(jsonPath("$.labels", hasSize(3)))
                .andExpect(jsonPath("$.labels[0].id", isOneOf(labelOne.getId(),
                        labelTwo.getId(), labelThree.getId())))

                .andExpect(jsonPath("$.labels[1].id", isOneOf(labelOne.getId(),
                        labelTwo.getId(), labelThree.getId())))

                .andExpect(jsonPath("$.labels[2].id", isOneOf(labelOne.getId(),
                        labelTwo.getId(), labelThree.getId())));
    }
}
