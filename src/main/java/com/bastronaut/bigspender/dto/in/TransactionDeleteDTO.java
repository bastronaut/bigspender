package com.bastronaut.bigspender.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TransactionDeleteDTO {

    private final List<Long> transactionIds;
}
