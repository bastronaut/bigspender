package com.bastronaut.bigspender.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping(value = "/{id}")
    public String getId(@PathVariable int id) {
        return "you got " + Integer.toString(id);
    }
}
