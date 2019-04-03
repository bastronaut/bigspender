package com.bastronaut.bigspender.controllers;

import com.bastronaut.bigspender.models.Transaction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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


    @PostMapping
    @ResponseBody
    public List<Transaction> postTransactions(
            @RequestParam(value = "file", required = true)List<MultipartFile> files) {
        System.out.println("yo");
        List result = new ArrayList<Transaction>();
        if (files.size() > 0) {
            result.add(new Transaction());
            return result;
        }
        return null;
    }



}
