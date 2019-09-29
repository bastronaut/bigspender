package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.UserRepository;
import com.bastronaut.bigspender.utils.SampleData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bastronaut.bigspender.utils.ApplicationConstants.DEFAULT_LABELCOLOR;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // clean db after each test
public class LabelServiceTest {

    @Autowired
    private LabelService labelService;

    @Autowired
    private UserRepository userRepository;

    private SampleData sampleData = new SampleData();

    private User user;

    @Before
    public void init() {
        // have to ensure user exists in DB to avoid
        this.user = new User("test@user.com", "password");
        userRepository.save(user);
    }

    @Test
    public void testCreateLabels() {

        final List<Label> emptyLabels = labelService.getLabels(user);
        assert(emptyLabels != null && emptyLabels.size() == 0);

        final Label labelOne = new Label("groceries", user, "#abc");

        final List<Label> newLabels = new ArrayList<>();
        newLabels.add(labelOne);

        final List<Label> savedLabels = labelService.saveLabels(newLabels);

        final List<Label> testLabels = labelService.getLabels(user);
        assert(testLabels != null && testLabels.size() == 1);
        final Label verifyLabel = testLabels.get(0);
        assert(StringUtils.equals(verifyLabel.getName(), "groceries"));
        assert(StringUtils.equals(verifyLabel.getUser().getUsername(), user.getUsername()));
        assert(StringUtils.equals(verifyLabel.getColor(), "#abc"));

        final Label labelTwo = new Label("groceries", user, "#123");
        final Label labelThree = new Label("clothing", user, "#6AF");
        final List<Label> newLabelsTwo = new ArrayList<>();
        newLabelsTwo.add(labelTwo);
        newLabelsTwo.add(labelThree);
        labelService.saveLabels(newLabelsTwo);

        final List<Label> testLabelsTwo = labelService.getLabels(user);
        assert(testLabelsTwo != null && testLabels.size() == 1);

    }

    @Test
    public void testDefaultLabelColor() {
        final String defaultColor = "#000";
        final Label l = new Label("groceries", user);
        final Label lTwo = new Label("shopping", user, "#FFF");
        List<Label> labels = new ArrayList<>();
        labels.add(l);
        labels.add(lTwo);
        labelService.saveLabels(labels);
        List<Label> getLabels = labelService.getLabels(user);
        assertEquals(defaultColor, getLabels.get(0).getColor());
        assertEquals("#FFF", getLabels.get(1).getColor());
    }
}
