package com.bastronaut.bigspender.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@Getter
public class ErrorDetails {

    private final String message;
    private List<String> details;
    private final LocalDate date;
}
