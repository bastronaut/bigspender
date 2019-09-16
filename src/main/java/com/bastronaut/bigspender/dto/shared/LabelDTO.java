package com.bastronaut.bigspender.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
public class LabelDTO {
    @NotEmpty(message = TODO)
    private String name;
    private String color;
}
