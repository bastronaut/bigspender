package com.bastronaut.bigspender.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class TransactionController {


    @GetMapping(value = "/{userid}/transactions",  produces = APPLICATION_JSON_VALUE)
    public String getTransaction(@PathVariable int userid) {
        return "returning " + Integer.toString(userid);
    }
}
