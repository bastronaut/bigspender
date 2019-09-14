package com.bastronaut.bigspender.integration;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.utils.SampleData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;



@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // required to reset state after test
@AutoConfigureMockMvc
@ContextConfiguration
public class LabelIntegrationTest {


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
