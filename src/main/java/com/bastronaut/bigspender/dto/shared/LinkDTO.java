package com.bastronaut.bigspender.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * A transaction can have any number of Labels assigned (linked) to it. The LinkDTO class contains
 * the transaction id, and label ids that must be linked or unlinked together. Can be used for both
 * adding links and removing links
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkDTO {
    @NotEmpty
    long transactionId;

    Set<Long> labelIds;
}
