package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.in.LabelAddDTO;
import com.bastronaut.bigspender.dto.in.LabelDeleteDTO;
import com.bastronaut.bigspender.dto.in.LabelGetResultDTO;
import com.bastronaut.bigspender.dto.in.LabelUpdateDTO;
import com.bastronaut.bigspender.dto.out.LabelAddResultDTO;
import com.bastronaut.bigspender.dto.out.LabelDeleteResultDTO;
import com.bastronaut.bigspender.dto.out.LabelUpdateResultDTO;
import com.bastronaut.bigspender.dto.shared.LabelDTO;
import com.bastronaut.bigspender.exceptions.LabelException;
import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.LabelService;
import com.bastronaut.bigspender.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bastronaut.bigspender.utils.ApplicationConstants.LABELS_ENDPOINT;
import static com.bastronaut.bigspender.utils.ApplicationConstants.LABELS_BY_TRANSACTION_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller for performing label related functionality, such as managing labels adn adding labels to transactions
 *
 */
@RestController
public class LabelController {

    private final LabelService labelService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    public LabelController(final LabelService labelService) {
        this.labelService = labelService;
    }


    @GetMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelGetResultDTO> getLabels(final @AuthenticationPrincipal User user) {
        final List<Label> labels = labelService.getLabels(user);
        List<LabelDTO> returnLabels = LabelDTO.fromLabels(labels);
        return ResponseEntity.status(HttpStatus.OK).body(new LabelGetResultDTO(returnLabels));
    }

    @GetMapping(path = LABELS_BY_TRANSACTION_ENDPOINT,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelGetResultDTO> getLabelsForTransaction(final @AuthenticationPrincipal User user,
                                                                     final @PathVariable @NotNull long transactionId) {
            final Set<Label> labelsById = labelService.getLabelsByTransactionId(transactionId, user);
            final List<Label> labelsReturn = new ArrayList<>(labelsById);
            final List<LabelDTO> labelsReturnDTO = LabelDTO.fromLabels(labelsReturn);
            return ResponseEntity.status(HttpStatus.OK).body(new LabelGetResultDTO(labelsReturnDTO));
        }

    @PostMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelAddResultDTO> createLabels(final @AuthenticationPrincipal User user,
                                                          @Valid @RequestBody final LabelAddDTO labelAddDTO,
                                                          final BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        final List<Label> labels = labelAddDTO.getLabels()
                .stream()
                .map(label -> Label.fromLabelDTO(label, user)).collect(Collectors.toList());

        final List<Label> savedLabels = labelService.saveLabels(labels);
        final List<LabelDTO> returnLabels = LabelDTO.fromLabels(savedLabels);

        return ResponseEntity.status(HttpStatus.OK).body(new LabelAddResultDTO(returnLabels));
    }


    @DeleteMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelDeleteResultDTO> deleteLabels(final @AuthenticationPrincipal User user,
                                                             @Valid @RequestBody final LabelDeleteDTO labelDeleteDTO,
                                                             final BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        final List<Long> ids = labelDeleteDTO.getLabelIds();
        final List<Label> deletedLabels = labelService.deleteLabels(ids, user);
        final List<LabelDTO> deletedLabelsDTO = LabelDTO.fromLabels(deletedLabels);

        return ResponseEntity.status(HttpStatus.OK).body(new LabelDeleteResultDTO(deletedLabelsDTO));
    }


    @PutMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelUpdateResultDTO> updateLabels(final @AuthenticationPrincipal User user,
                                                             @Valid @RequestBody final LabelUpdateDTO labelUpdateDTO,
                                                             final BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        final List<Label> updatedLabels = labelService.updateLabels(labelUpdateDTO.getLabels(), user);
        final List<LabelDTO> updatedLabelsDTO = LabelDTO.fromLabels(updatedLabels);
        return ResponseEntity.status(HttpStatus.OK).body(new LabelUpdateResultDTO(updatedLabelsDTO));
    }

    private void checkBindingErrors(final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0);
            throw new LabelException(error.getDefaultMessage());
        }
    }

}
