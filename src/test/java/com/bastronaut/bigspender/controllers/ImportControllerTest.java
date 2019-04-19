package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.exceptions.ErrorDetails;
import com.bastronaut.bigspender.exceptions.TransactionImportException;
import com.bastronaut.bigspender.utils.JsonResponseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;

import static com.bastronaut.bigspender.utils.TestConstants.SUBSET_SAMPLE_CSV_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImportControllerTest {


    private static final String IMPORT_POST_ENDPOINT = "/users/1/transactionimport";
    private static final String ERROR_MSG_FIELD = "message";
    private static final String ERROR_MSG = "Transaction import error";
    private static final String ERROR_DETAILS_FIELD = "details";
    private static final String ERROR_DETAILS = "No file was posted with parametername 'file'";
    private static final String ERROR_DATE_FIELD = "date";

    private static final String IMPORT_COUNT_FIELD = "importCount";
    private static final String IMPORT_DATE_FIELD = "importDate";
    private static final String IMPORT_TXS_FIELD = "transactions";

    @Autowired
    private ImportController importController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() throws Exception {
        assertThat(importController).isNotNull();
    }

    @WithMockUser(username="henk@email.com", password = "test")
    @Test
    public void testPostTransactionsSuccess() throws Exception {

        File sampleFile = new File(SUBSET_SAMPLE_CSV_PATH);
        FileInputStream input = new FileInputStream(sampleFile);

        MockMultipartFile sampleCSV = new MockMultipartFile("file", sampleFile.getName(),
                "multipart/form-data", input);

        final MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.multipart(IMPORT_POST_ENDPOINT)
                .file(sampleCSV))
                .andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());
        final JsonNode parsedResponse = JsonResponseUtil.getJsonFromResponseContent(response.getContentAsString());
        assertEquals(parsedResponse.get(IMPORT_COUNT_FIELD).asText(), "5");
        assertEquals(parsedResponse.get(IMPORT_DATE_FIELD).asText(), LocalDate.now().toString());
        final JsonNode txs = parsedResponse.get(IMPORT_TXS_FIELD);

        final JsonNode firstTx = parsedResponse.get(0);
        // Sample tests, full import is tested in Import Service test
        assertTrue(firstTx.get("amount").equals("180"));
        assertTrue(firstTx.get("name").equals("AH to go 5869 DenHaa"));
        assertTrue(firstTx.get("accountNumber").equals("NL41INGB0006212385"));
        assertTrue(firstTx.get("statement").equals("Pasvolgnr: 008 01-04-2019 22:39 Valutadatum: 02-04-2019"));
        assertTrue(firstTx.get("code").equals("GT"));
    }

    @WithMockUser
    @Test
    public void testPostTransactionsWithoutFile() throws Exception {

        final MockHttpServletResponse response = mockMvc.perform(post(IMPORT_POST_ENDPOINT)).andReturn().getResponse();
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());
        final JsonNode parsedResponse = JsonResponseUtil.getJsonFromResponseContent(response.getContentAsString());
        final String message = parsedResponse.get(ERROR_MSG_FIELD).asText();
        final String details = parsedResponse.get(ERROR_DETAILS_FIELD).asText();
        final String date = parsedResponse.get(ERROR_DATE_FIELD).asText();
        assertTrue(StringUtils.equals(message, ERROR_MSG));
        assertTrue(StringUtils.equals(details, ERROR_DETAILS));
        assertTrue(StringUtils.equals(date, LocalDate.now().toString()));
    }

}