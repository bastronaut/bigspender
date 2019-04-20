package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.TransactionImportDTO;
import com.bastronaut.bigspender.dto.UserRegistrationDTO;
import com.bastronaut.bigspender.exceptions.TransactionImportException;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.INGTransactionParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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

/**
 * Controller for /import/{userid}/transactions/ endpoint, responsible for allowing users to upload
 * their data into the system
 *
 * TODO: hardcoded the ing importer, could add a post param for bank type and use the correct importer
 */

@RestController
@RequestMapping(path = TRANSACTION_IMPORT_ENDPOINT,  produces = APPLICATION_JSON_VALUE)
public class ImportController {

    private Logger logger = LoggerFactory.getLogger(ImportController.class);

    @Autowired
    private INGTransactionParserImpl importer;

    /**
     * POST endpoint for a CSV file of transactions
     * @param files requires a key named: "file" and a CSV attached
     * @return a DTO result of the transaction import, containing all of the transactions that were
     * imported. (consumes = "multipart/form-data")
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<TransactionImportDTO> postTransactions(@AuthenticationPrincipal User user,
            @RequestParam(value = "file", required = false) final List<MultipartFile> files) {

        if (files != null && files.size() > 0) {
            try {
                InputStream file = files.get(0).getInputStream();

                TransactionImport parsedTransactions = importer.parseTransactions(file, user);
                TransactionImportDTO transactionImportDTO = convertToDTO(parsedTransactions);
                return ResponseEntity.status(HttpStatus.OK).body(transactionImportDTO);

            } catch (IOException e) {
                logger.info("Error getting and parsing CSV from POST request", e);
            }
        }
        throw new TransactionImportException("No file was posted with parametername 'file'");
    }


    private TransactionImportDTO convertToDTO(TransactionImport transactionImport) {
        return new TransactionImportDTO(transactionImport);
    }


    private User convertToEntity(UserRegistrationDTO userRegistrationDTO) {
        return new User(userRegistrationDTO.getEmail(), userRegistrationDTO.getName(), userRegistrationDTO.getPassword());
    }



}
