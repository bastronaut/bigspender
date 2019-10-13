package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@Getter
public class LabelGetForTransactionResultDTO {
    final List<LabelDTO> labels;
    final long transactionId;
}