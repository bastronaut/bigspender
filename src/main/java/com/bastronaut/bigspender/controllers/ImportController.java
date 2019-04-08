package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.dto.TransactionImportDTO;
import com.bastronaut.bigspender.dto.UserDTO;
import com.bastronaut.bigspender.dto.UserRegistrationDTO;
import com.bastronaut.bigspender.models.TransactionImport;
import com.bastronaut.bigspender.models.User;
import com.bastronaut.bigspender.services.INGTransactionImporterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTION_IMPORT_ENDPOINT;

/**
 * Controller for /import/{userid}/transactions/ endpoint, responsible for allowing users to upload
 * their data into the system
 *
 * TODO: hardcoded the ing importer, could add a post param for bank type and use the correct importer
 */

@RestController
@RequestMapping(path = TRANSACTION_IMPORT_ENDPOINT)
public class ImportController {

    Logger logger = LoggerFactory.getLogger(ImportController.class);

    @Autowired
    INGTransactionImporterImpl importer;

    /**
     * POST endpoint for a CSV file of transactions
     * @param files requires a key named: "file" and a CSV attached
     * @return a DTO result of the transaction import, containing all of the transactions that were
     * imported.
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<TransactionImportDTO> postTransactions(
            @RequestParam(value = "file", required = false) List<MultipartFile> files){
        //            @RequestBody UserRegistrationDTO userRegistrationDTO) {

        HttpHeaders responseHeaders = new HttpHeaders();

        if (files != null && files.size() > 0) {
            try {
                InputStream file = files.get(0).getInputStream();
//                User user = convertToEntity(userRegistrationDTO);
                User user = new User("test", "test", "test");
                TransactionImport parsedTransactions = importer.parseTransactions(file, user);
                TransactionImportDTO transactionImportDTO = convertToDTO(parsedTransactions);
                return ResponseEntity.status(HttpStatus.OK).body(transactionImportDTO);

            } catch (IOException e) {
                logger.info("Error getting and parsing CSV from POST request", e);
            }
        }
        return new ResponseEntity("error todo", HttpStatus.BAD_REQUEST);
    }


    private TransactionImportDTO convertToDTO(TransactionImport transactionImport) {
        return new TransactionImportDTO(transactionImport);
    }

    private UserDTO convertToUserDTO() {
        return null;
    }

    private User convertToEntity(UserRegistrationDTO userRegistrationDTO) {
        return new User(userRegistrationDTO.getEmail(), userRegistrationDTO.getFirstName(), userRegistrationDTO.getPassword());
    }



}
