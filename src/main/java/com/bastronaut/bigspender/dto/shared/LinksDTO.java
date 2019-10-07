package com.bastronaut.bigspender.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.bastronaut.bigspender.utils.ApplicationConstants.ERRORMSG_LINKS_EMPTY;

/**
 * Container class for LinkDTO
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class LinksDTO {

    @NotNull(message = ERRORMSG_LINKS_EMPTY)
    List<LinkDTO> links;

}
