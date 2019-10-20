package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;


@AllArgsConstructor
@Getter
public class LabelsByTransactionIdResultDTO {
    final Set<LabelDTO> labels;
    final long transactionId;
}