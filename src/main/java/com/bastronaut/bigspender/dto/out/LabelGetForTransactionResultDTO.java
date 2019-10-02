package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@AllArgsConstructor
@Data
public class LabelGetForTransactionResultDTO {
    List<LabelDTO> labels;
    long transactionId;
}