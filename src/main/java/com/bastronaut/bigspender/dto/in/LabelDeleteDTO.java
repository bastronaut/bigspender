package com.bastronaut.bigspender.dto.in;

import com.bastronaut.bigspender.models.Label;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_LABEL_REMOVE_EMPTY;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LabelDeleteDTO {
    @Valid
    @NotEmpty(message = ERRORMSG_LABEL_REMOVE_EMPTY)
    List<LabelDeleteIdDTO> labels;

    public List<Long> getLabelIds() {
        return labels.stream().map(LabelDeleteIdDTO::getId).collect(Collectors.toList());
    }
}
