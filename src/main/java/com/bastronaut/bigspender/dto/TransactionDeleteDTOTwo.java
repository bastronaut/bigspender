package com.bastronaut.bigspender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class TransactionDeleteDTOTwo {

    private final long deleted;
    private String[] transactionIds;

    public TransactionDeleteDTOTwo(long deleted, String[] transactionIds) {
        this.deleted = deleted;
        this.transactionIds = transactionIds;
    }
}
