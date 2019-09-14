package com.bastronaut.bigspender.services;

import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.repositories.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    private final LabelRepository labelRepository;

    @Autowired
    public LabelService(LabelRepository labelRepository) {
        this.labelRepository= labelRepository;
    }

    public List<Label> getLabels(final User user) {
        return labelRepository.findAllByUser(user);
    }

    public List<Label> saveLabels(final List<Label> labels) {
        return labelRepository.saveAll(labels);
    }

    public Label saveLabel(final Label label) {
        return labelRepository.save(label);
    }

}
