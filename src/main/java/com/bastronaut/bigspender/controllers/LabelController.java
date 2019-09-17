package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.in.LabelAddDTO;
import com.bastronaut.bigspender.dto.out.LabelAddResultDTO;
import com.bastronaut.bigspender.dto.shared.LabelDTO;
import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelAddResultDTO> createLabels(final @AuthenticationPrincipal User user,
                                                          @RequestBody final LabelAddDTO labelAddDTO) {


        final List<Label> labels = labelAddDTO.getLabels()
                .stream()
                .map(label -> Label.fromLabelDTO(label, user)).collect(Collectors.toList());

        final List<Label> savedLabels = labelService.saveLabels(labels);

        final List<LabelDTO> returnLabels = savedLabels.stream()
                .map(label -> LabelDTO.fromLabel(label))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(new LabelAddResultDTO(returnLabels));
    }


}
