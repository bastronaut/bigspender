package com.bastronaut.bigspender.exceptions;

import lombok.AllArgsConstructor;

import java.time.LocalDate;


@AllArgsConstructor
public class ErrorDetails {

    private  final String message;
    private final String details;
    private final LocalDate date;
}
