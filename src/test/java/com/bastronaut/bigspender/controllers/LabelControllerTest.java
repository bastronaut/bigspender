package com.bastronaut.bigspender.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.bastronaut.bigspender.utils.SampleData.HEADER_ENCODED_NONEXISTINGUSER;
import static com.bastronaut.bigspender.utils.TestConstants.LABELSID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.LABELS_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.LABEL_ENDPOINT;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTIONID_PARAM_REPLACE;
import static com.bastronaut.bigspender.utils.TestConstants.TRANSACTION_ENDPOINT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // required to reset state after test
public class LabelControllerTest {

    @Autowired
    private LabelController labelController;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }


    /**
     * Attempts to perform deletion with incorrect auth and missing auth
     * @throws Exception
     */
    @Test
    public void testGetLabelsNotAuthorized() throws Exception {

        final String endpoint = LABELS_ENDPOINT;
        mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.get(endpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    /**
     * Attempts to perform deletion with incorrect auth and missing auth
     * @throws Exception
     */
    @Test
    public void testAddLabelsNotAuthorized() throws Exception {

        final String endpoint = LABELS_ENDPOINT;
        mockMvc.perform(MockMvcRequestBuilders.post(endpoint))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.post(endpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



    /**
     * Attempts to perform deletion with incorrect auth and missing auth
     * @throws Exception
     */
    @Test
    public void testDeleteLabelsNotAuthorized() throws Exception {

        final String endpoint = LABEL_ENDPOINT.replace(LABELSID_PARAM_REPLACE, "1");
        mockMvc.perform(MockMvcRequestBuilders.delete(endpoint))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.delete(endpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    /**
     * Attempts to perform deletion with incorrect auth and missing auth
     * @throws Exception
     */
    @Test
    public void testUpdateLabelsNotAuthorized() throws Exception {

        final String endpoint = LABEL_ENDPOINT.replace(LABELSID_PARAM_REPLACE, "1");
        mockMvc.perform(MockMvcRequestBuilders.put(endpoint))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.put(endpoint)
                .header(HttpHeaders.AUTHORIZATION, HEADER_ENCODED_NONEXISTINGUSER))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


}
