package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.Transaction;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.services.INGTransactionImporterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_IMPORT_POST;

/**
 * Controller for /import/<userid>/transactions/ endpoint, responsible for allowing users to upload
 * their data into the system
 */

@RestController
@RequestMapping(path = TRANSACTION_IMPORT_POST)
public class ImportController {

    Logger logger = LoggerFactory.getLogger(ImportController.class);

    // TODO read from request which bank importer is required and inject correct importer
    // add logic for bank type
    @Autowired
    INGTransactionImporterImpl importer;

    /**
     *
     * @param files
     * @return
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<List<Transaction>> postTransactions(
            @RequestParam(value = "file", required = false) List<MultipartFile> files) {

        HttpHeaders responseHeaders = new HttpHeaders();

        TransactionImport result;

        if (files != null && files.size() > 0) {

            try {
                InputStream file = files.get(0).getInputStream();
                result = importer.parseTransactions(file);
                return ResponseEntity.status(HttpStatus.OK).body(result.getTransactions());

            } catch (IOException e) {
                logger.info("Error getting inputstream from POST MultipartFile", e);
            }
        }
        return new ResponseEntity("error todo", HttpStatus.BAD_REQUEST);
    }



}
