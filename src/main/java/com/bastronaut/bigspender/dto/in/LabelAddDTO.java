package com.bastronaut.bigspender.dto.in;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@AllArgsConstructor
@Getter
public class LabelAddDTO {

    private List<LabelDTO> labels;
}
