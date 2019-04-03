package com.bastronaut.bigspender.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileInputStream;

import static com.bastronaut.bigspender.utils.TestConstants.SUBSET_SAMPLE_CSV_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImportControllerTest {


    private static final String IMPORT_POST_ENDPOINT = "/import/1/transactions";

    @Autowired
    private ImportController importController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() throws Exception {
        assertThat(importController).isNotNull();
    }

    @Test
    public void testPostTransactionsSuccess() throws Exception {

        File sampleFile = new File(SUBSET_SAMPLE_CSV_PATH);
        FileInputStream input = new FileInputStream(sampleFile);

        MockMultipartFile sampleCSV = new MockMultipartFile("file", sampleFile.getName(),
                "multipart/form-data", input);

        mockMvc.perform(MockMvcRequestBuilders.multipart(IMPORT_POST_ENDPOINT)
                .file(sampleCSV))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testPostTransactionsWithoutFile() throws Exception {

        mockMvc.perform(post(IMPORT_POST_ENDPOINT))
                .andExpect(status().isBadRequest());
    }

}