package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LabelRepository;
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

    // TODO
    // define what create label should look like in request
    @Test
    public void testCreateLabels() throws Exception {
        final String endpoint = StringUtils.replace(LABELS_ENDPOINT, USERID_PARAM_REPLACE, userIdTestUserOne);
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
        final String endpoint = StringUtils.replace(LABELS_ENDPOINT, USERID_PARAM_REPLACE, userIdTestUserOne);

        final String createLabelsMissingLabelsJson = MockJsonReader
                .readMockJsonAsString("testCreateLabelsMissingLabelsJson.json");

        mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createLabelsMissingLabelsJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(LABEL_ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERRORMSG_LABEL_EMPTY))
                .andReturn();


        final String createLabelsMissingNameJson = MockJsonReader
                .readMockJsonAsString("testCreateLabelsMissingNameJson.json");

        mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
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
        final String endpoint = StringUtils.replace(LABELS_ENDPOINT, USERID_PARAM_REPLACE, userIdTestUserOne);

        // Setup labels to update
        final Label labelOne = new Label("groceries", testUserOne, "#111");
        final Label labelTwo = new Label("groceries", testUserOne, "#111");
        final Label labelThree = new Label("groceries", testUserOne, "#111");
        labelRepository.save(labelOne);
        labelRepository.save(labelTwo);
        labelRepository.save(labelThree);

        final String testUpdateLabelsJson = MockJsonReader
                .readMockJsonAsString("testUpdateLabels.json");

        mockMvc.perform(MockMvcRequestBuilders.put(endpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUpdateLabelsJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(4)))
                .andExpect(jsonPath("$.labels[0].name").value("groceries-new"))
                .andReturn();

    }

    @Test
    public void testAddLabelsToTransaction() throws Exception {

    }

    @Test
    public void testAddLabelsToTransactions() throws Exception {

    }



}
