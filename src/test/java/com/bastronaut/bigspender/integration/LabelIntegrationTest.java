package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.UserRepository;
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

import static com.bastronaut.bigspender.utils.TestConstants.LABELS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.USERID_PARAM_REPLACE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


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

        mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .header(HttpHeaders.AUTHORIZATION, headerEncodedUserOne)
                .param("", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content("");
//                .andDo(print())
//                .andExpect()
    }

    @Test
    public void testAddLabelsToTransaction() throws Exception {

    }

    @Test
    public void testAddLabelsToTransactions() throws Exception {

    }

    @Test
    public void testCreateLabel() throws Exception {
        final User user = SampleData.TESTUSERONE;
        final String testLabelName = "groceries";
        final String testLabelColor = "#123";

        final Label testLabelOne = new Label("shopping", user);



    }

}
