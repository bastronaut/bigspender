package com.bastronaut.bigspender.dto.in;

import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor // @RequestBody serialization
@Getter
public class LabelAddDTO {
    @NotEmpty
    private List<LabelDTO> labels;

}
