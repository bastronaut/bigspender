package com.bastronaut.bigspender.dto.shared;

import com.bastronaut.bigspender.models.Label;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_LABEL_NAME_NULL;

@AllArgsConstructor
@NoArgsConstructor // @RequestBody injection
@ToString
@Getter
public class LabelDTO {
    @NotEmpty(message = ERRORMSG_LABEL_NAME_NULL)
    private String name;
    private String color;
    private long id;

    public LabelDTO(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public LabelDTO(final String name) {
        this.name = name;
    }


    public static LabelDTO fromLabel(final Label label) {
        return new LabelDTO(label.getName(), label.getColor(), label.getId());
    }
}
