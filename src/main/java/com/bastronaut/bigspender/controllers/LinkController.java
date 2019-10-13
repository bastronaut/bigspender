package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.shared.LinksDTO;
import com.bastronaut.bigspender.exceptions.LinkLabelException;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_LABELS_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class LinkController {

    private final LabelService labelService;

    @Autowired
    public LinkController(final LabelService labelService) {
        this.labelService = labelService;
    }


    @PostMapping(path = TRANSACTION_LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LinksDTO> linkLabelsToTransaction(final @AuthenticationPrincipal User user,
                                                            final @Valid @RequestBody LinksDTO linksDTO,
                                                            final BindingResult bindingResult) {
        checkBindingErrors(bindingResult);

        final LinksDTO result = labelService.linkLabelsToTransactions(linksDTO, user);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping(path = TRANSACTION_LABELS_ENDPOINT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<LinksDTO> unlinkLabelsFromTransaction(final @AuthenticationPrincipal User user,
                                                              final @Valid @RequestBody LinksDTO linksDTO,
                                                              final BindingResult bindingResult) {

        checkBindingErrors(bindingResult);

        final LinksDTO result = labelService.unlinkLabelsFromTransactions(linksDTO, user);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }




    private void checkBindingErrors(final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError error = bindingResult.getFieldErrors().get(0);
            throw new LinkLabelException(error.getDefaultMessage());
        }
    }
}
