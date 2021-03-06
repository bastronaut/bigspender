package com.bastronaut.bigspender.dto.out;

import com.bastronaut.bigspender.dto.in.LabelAddDTO;
import com.bastronaut.bigspender.dto.shared.LabelDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
public class LabelAddResultDTO {

    List<LabelDTO> labels;
}
