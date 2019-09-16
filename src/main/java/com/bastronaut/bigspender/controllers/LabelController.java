package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.in.LabelAddDTO;
import com.bastronaut.bigspender.dto.out.LabelAddResultDTO;
import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bastronaut.bigspender.utils.ApplicationConstants.LABELS_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for performing label related functionality, such as managing labels adn adding labels to transactions
 *
 */
@RestController
public class LabelController {

    private final LabelService labelService;

    @Autowired
    public LabelController(final LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping(path = LABELS_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelAddResultDTO> createLabels(final @AuthenticationPrincipal User user,
                                                          final LabelAddDTO labelAddDTO) {
        final List<Label> labels = labelAddDTO.getLabels()
                .stream()
                .map(label -> Label.fromLabelDTO(label, user)).collect(Collectors.toList());
    }


}
