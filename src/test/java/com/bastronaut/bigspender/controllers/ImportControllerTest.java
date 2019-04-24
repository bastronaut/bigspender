package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.exceptions.ErrorDetails;
import com.bastronaut.bigspender.exceptions.TransactionImportException;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.services.INGTransactionParserImpl;
import com.bastronaut.bigspender.utils.JsonResponseUtil;
import com.bastronaut.bigspender.utils.SampleData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.tools.rmi.Sample;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static com.bastronaut.bigspender.utils.TestConstants.SUBSET_SAMPLE_CSV_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests are somewhat of an integration test also
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImportControllerTest {


    private static final String IMPORT_POST_ENDPOINT = "/users/1/transactionimport";
    private static final String ERROR_MSG_FIELD = "message";
    private static final String ERROR_MSG = "Transaction Import error";
    private static final String ERROR_DETAILS_FIELD = "details";
    private static final String ERROR_DETAILS = "No file was posted with parametername 'file'";
    private static final String ERROR_DATE_FIELD = "date";

    private static final String IMPORT_COUNT_FIELD = "importCount";
    private static final String IMPORT_DATE_FIELD = "importDate";
    private static final String IMPORT_TXS_FIELD = "transactions";

    @Autowired
    private ImportController importController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private INGTransactionParserImpl importer;

    private User user;


    @Before
    public void init() throws IOException {
        this.user = SampleData.getTestUser();
        userRepository.save(this.user);

        given(importer.parseTransactions(any(), any())).willReturn(SampleData.getTransactionImport());
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(importController).isNotNull();
    }

    @WithMockUser
    @Test
    public void testPostTransactionsSuccess() throws Exception {
        File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
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

        final JsonNode firstTx = txs.get(0);
        // Sample tests, full import is tested in Import Service test
        assertEquals("180", firstTx.get("amount").asText());
        assertEquals("AH to go 5869 DenHaa", firstTx.get("name").asText());
        assertEquals("NL41INGB0006212385", firstTx.get("accountNumber").asText());
        assertEquals("Pasvolgnr: 008 01-04-2019 22:39 Transactie: I4J4E2 Term: 648B5Z Valutadatum: 02-04-2019", firstTx.get("statement").asText());
        assertEquals("BA", firstTx.get("code").asText());

        final JsonNode lastTx = txs.get(4);
        assertEquals(LocalDate.of(2019, 04, 01).toString(), lastTx.get("date").asText());
        assertEquals( "14:20:00", lastTx.get("time").asText());
        assertEquals("BETAALAUTOMAAT", lastTx.get("mutationType").asText());
        assertEquals("MONDAY", lastTx.get("day").asText());
        assertEquals("BIJ", lastTx.get("type").asText());
        assertEquals("null", lastTx.get("receivingAccountNumber").asText());
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