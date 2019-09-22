package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.in.LabelAddDTO;
import com.bastronaut.bigspender.dto.in.LabelDeleteDTO;
import com.bastronaut.bigspender.dto.in.LabelUpdateDTO;
import com.bastronaut.bigspender.dto.out.LabelAddResultDTO;
import com.bastronaut.bigspender.dto.out.LabelDeleteResultDTO;
import com.bastronaut.bigspender.dto.out.LabelUpdateResultDTO;
import com.bastronaut.bigspender.dto.shared.LabelDTO;
import com.bastronaut.bigspender.exceptions.LabelException;
import com.bastronaut.bigspender.models.Label;
import com.bastronaut.bigspender.models.Transaction;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bastronaut.bigspender.utils.ApplicationConstants.LABELS_ENDPOINT;
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

    @PostMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelAddResultDTO> createLabels(final @AuthenticationPrincipal User user,
                                                          @Valid @RequestBody final LabelAddDTO labelAddDTO,
                                                          final BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        final List<Label> labels = labelAddDTO.getLabels()
                .stream()
                .map(label -> Label.fromLabelDTO(label, user)).collect(Collectors.toList());

        final List<Label> savedLabels = labelService.saveLabels(labels);

        final List<LabelDTO> returnLabels = savedLabels.stream()
                .map(label -> LabelDTO.fromLabel(label))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(new LabelAddResultDTO(returnLabels));
    }


    @DeleteMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelDeleteResultDTO> deleteLabels(final @AuthenticationPrincipal User user,
                                                             @Valid @RequestBody final LabelDeleteDTO labelDeleteDTO,
                                                             final BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        final List<Long> ids = labelDeleteDTO.getLabelIds();
        final List<Label> labels = labelService.getLabelsById(ids, user);

        for (Label label: labels) {
            List<Transaction> transactions = label.getTransactions();
            for (Transaction transaction: transactions) {
                transaction.getLabels().remove(label);
            }// if size >0 save
            transactionService.saveTransactions(transactions);
        }

        final List<Label> deletedLabels = labelService.deleteLabels(ids, user);


        final List<LabelDTO> deletedLabelsDTO = deletedLabels.stream()
                .map(LabelDTO::fromLabel)
                .collect(Collectors.toList());

        final LabelDeleteResultDTO result = new LabelDeleteResultDTO(deletedLabelsDTO);

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @PutMapping(path = LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelUpdateResultDTO> updateLabels(final @AuthenticationPrincipal User user,
                                                             @Valid @RequestBody final LabelUpdateDTO labelDeleteDTO,
                                                             final BindingResult bindingResult) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new LabelUpdateResultDTO(new ArrayList<>()));
    }

    private void checkBindingErrors(final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0);
            throw new LabelException(error.getDefaultMessage());
        }
    }

}
