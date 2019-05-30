package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TransactionDeleteResultDTO {
    private final int deleted;
    private List<Long> transactionIds = new ArrayList<>();


    public TransactionDeleteResultDTO(final List<Transaction> transactions) {
        this.deleted = transactions.size();

        if (transactions != null) {
            transactions.stream().forEach(t -> transactionIds.add(t.getId()));
        }
    }


}
