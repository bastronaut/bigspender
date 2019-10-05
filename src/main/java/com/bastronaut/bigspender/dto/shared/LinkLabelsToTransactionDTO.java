package com.bastronaut.bigspender.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkLabelsToTransactionDTO {
    @NotEmpty
    long transactionId;

    Set<Long> labelIds;
}
