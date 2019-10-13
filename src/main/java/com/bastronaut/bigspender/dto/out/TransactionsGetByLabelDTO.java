package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class TransactionsGetByLabelDTO {

    private long labelId;
    private Set<TransactionDTO> transactions;
}
