package com.bastronaut.bigspender.dto.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class TransactionDeleteDTO {
    private final long deleted;
    private String[] transactionIds;

    public TransactionDeleteDTO(final long deleted) {
        this.deleted = deleted;
    }

    // For requests to delete multiple transactions, we return the transactions that have been deleted
    public TransactionDeleteDTO(final long deleted, final String[] transactionIds) {
        this.deleted = deleted;
        this.transactionIds = transactionIds;
    }


}
