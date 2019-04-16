package com.bastronaut.bigspender.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;


@AllArgsConstructor
@Getter
public class ErrorDetails {

    private final String message;
    private final String details;
    private final LocalDate date;
}
