package com.bastronaut.bigspender.dto.in;


import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_LABEL_EMPTY;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LabelUpdateDTO {
    @Valid
    @NotEmpty(message = ERRORMSG_LABEL_EMPTY)
    private List<LabelDTO> labels;
}
