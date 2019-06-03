package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.in.TransactionImportDTO;
import com.bastronaut.bigspender.dto.out.TransactionImportResultDTO;
import com.bastronaut.bigspender.dto.in.UserRegistrationDTO;
import com.bastronaut.bigspender.exceptions.TransactionImportException;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.INGTransactionParserImpl;
import com.bastronaut.bigspender.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_IMPORT_ENDPOINT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * Controller for /import/{userid}/transactions/ endpoint, responsible for allowing users to upload
 * their data into the system
 *
 * TODO: hardcoded the ing transactionParser, could add a post param for bank type and use the correct transactionParser
 */

@RestController
@RequestMapping(path = TRANSACTION_IMPORT_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
public class ImportController {

    private Logger logger = LoggerFactory.getLogger(ImportController.class);

    @Autowired
    private INGTransactionParserImpl transactionParser;

    @Autowired
    private TransactionService transactionService;

    /**
     * POST endpoint for a CSV file of transactions
     *
     * @param user The user to perform the import for
     * @param transactionImportDTO the model object for specifying additional import details
     * @param uploaded requires a key named: "file" and a CSV attached
     * @return a DTO result of the transaction import, containing all of the transactions that were
     * imported. (consumes = "multipart/form-data")
     *
     * TODO: move parsing to a parsing service that can pick the specific bank once multiple bank import is implemented
     * TODO: ideal would be to package the Multipart File into the DTO, bu can't figure it out yet
     * @return
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<TransactionImportResultDTO> postTransactions(@AuthenticationPrincipal final User user,
                                                                       final TransactionImportDTO transactionImportDTO,
                                                                       @RequestParam(value = "file", required = false) final MultipartFile uploaded) {

        if (uploaded != null) {
            try {
                final InputStream file = uploaded.getInputStream();

                final TransactionImport parsedTransactions = transactionParser.parseTransactions(file, user);
                final TransactionImport importedTransactions = transactionService.saveTransactionImport(parsedTransactions);
                final TransactionImportResultDTO transactionImportResultDTO = TransactionImportResultDTO.fromTransactionImport(importedTransactions);
                return ResponseEntity.status(HttpStatus.OK).body(transactionImportResultDTO);

            } catch (IOException e) {
                logger.info("Error getting and parsing CSV from POST request", e);
            }
        }
        throw new TransactionImportException("No file was posted with parametername 'file'");
    }

}
