package com.bastronaut.bigspender.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_LABEL_REMOVE_ID_EMPTY;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LabelDeleteIdDTO {
    Long id;
}
