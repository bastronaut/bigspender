package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.services.INGTransactionParserImpl;
import com.bastronaut.bigspender.services.TransactionService;
import com.bastronaut.bigspender.utils.SampleData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;

import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DATE_FIELD;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_DETAILS_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.ERROR_MESSAGE_PARAM;
import static com.bastronaut.bigspender.utils.TestConstants.FAKE_TRANSACTIONS_CSV_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests are somewhat of an integration test also
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ImportControllerTest {


    private static final String IMPORT_POST_ENDPOINT = "/users/1/transactionimport";
    private static final String ERROR_MSG = "Transaction Import error";
    private static final String ERROR_DETAILS = "No file was posted with parametername 'file'";


    @Autowired
    private ImportController importController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private INGTransactionParserImpl importer;

    @MockBean
    private TransactionService transactionService;

    private User user;

    private SampleData sampleData = new SampleData();

    @Before
    public void init() throws IOException {
        this.user = sampleData.getTestUserOne();
        userRepository.save(this.user);

        given(importer.parseTransactions(any(), any())).willReturn(sampleData.getSampleTransactionImport());
        given(transactionService.saveTransactionImport(any())).willReturn(sampleData.getSampleTransactionImport());
    }

    @WithMockUser
    @Test
    public void testImportTransactionsSuccess() throws Exception {
        final File sampleFile = new File(FAKE_TRANSACTIONS_CSV_PATH);
        final FileInputStream input = new FileInputStream(sampleFile);

        final MockMultipartFile sampleCSV = new MockMultipartFile("file", sampleFile.getName(),
                "multipart/form-data", input);

        final MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.multipart(IMPORT_POST_ENDPOINT)
                .file(sampleCSV))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[:1].date").value("2019-04-01"))
                .andExpect(jsonPath("$.transactions[:1].time").value("22:39:00"))
                .andExpect(jsonPath("$.transactions[:1].accountNumber").value("NL41INGB0006212385"))
                .andExpect(jsonPath("$.transactions[:1].code").value("GT"))
                .andExpect(jsonPath("$.transactions[:1].type").value("Af"))
                .andExpect(jsonPath("$.transactions[6:].receivingAccountNumber").value("NL20INGB0001987654"))
                .andExpect(jsonPath("$.transactions[6:].amount").value(1980))
                .andExpect(jsonPath("$.transactions[6:].mutationType").value("Diversen"))
                .andExpect(jsonPath("$.user.email").value("test@email.com"))
                .andExpect(jsonPath("$.transactions[6:].statement").value("Pasvolgnr: 008 01-04-2019 07:25 Valutadatum: 02-04-2019"))
                .andExpect(jsonPath("$.transactions[6:].day").value(7))
                .andReturn().getResponse();
    }

    @WithMockUser
    @Test
    public void testImportTransactionsWithoutFile() throws Exception {
        mockMvc.perform(post(IMPORT_POST_ENDPOINT))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(ERROR_MESSAGE_PARAM).value(ERROR_MSG))
                .andExpect(jsonPath(ERROR_DETAILS_PARAM).value(ERROR_DETAILS))
                .andExpect(jsonPath(ERROR_DATE_FIELD).value(LocalDate.now().toString()))
                .andReturn().getResponse();
    }

}