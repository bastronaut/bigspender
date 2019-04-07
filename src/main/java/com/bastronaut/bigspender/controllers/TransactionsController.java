package com.bastronaut.bigspender.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController {


    @GetMapping(value = "/{userid}/transactions")
    public String getTransaction(@PathVariable int userid) {
        return "returning " + Integer.toString(userid);
    }
}
