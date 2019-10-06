package com.bastronaut.bigspender.dto.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Container class for LinkDTO
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class LinksDTO {

    List<LinkDTO> links;

}
