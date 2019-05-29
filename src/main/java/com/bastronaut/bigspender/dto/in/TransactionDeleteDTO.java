package com.bastronaut.bigspender.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class TransactionDelete {

    ;private String[] transactionIds;

    public TransactionDelete(String[] transactionIds) {
        this.transactionIds = transactionIds;
    }
}
