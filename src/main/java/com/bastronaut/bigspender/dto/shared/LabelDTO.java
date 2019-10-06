package com.bastronaut.bigspender.dto.shared;

import com.bastronaut.bigspender.models.Label;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_LABEL_NAME_EMPTY;

@AllArgsConstructor
@NoArgsConstructor // @RequestBody injection
@ToString
@Getter
public class LabelDTO {
    @NotEmpty(message = ERRORMSG_LABEL_NAME_EMPTY)
    private String name;
    @Size(max = 7)
    private String color;
    private long id;

    public LabelDTO(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public LabelDTO(final String name) {
        this.name = name;
    }


    public static LabelDTO fromLabels(final Label label) {
        return new LabelDTO(label.getName(), label.getColor(), label.getId());
    }

    public static List<LabelDTO> fromLabels(final List<Label> labels) {
        return labels.stream().map(LabelDTO::fromLabels).collect(Collectors.toList());
    }

    public static Set<LabelDTO> fromLabels(final Set<Label> labels) {
        return labels.stream().map(LabelDTO::fromLabels).collect(Collectors.toSet());
    }
}
