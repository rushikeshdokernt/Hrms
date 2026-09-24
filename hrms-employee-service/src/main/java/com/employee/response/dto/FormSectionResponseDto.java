package com.employee.response.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormSectionResponseDto {

    private UUID formSectionId;
    private String sectionName;
    private Boolean visible;
    private Integer sortOrder;
    private Boolean immutable;
    private List<FormFieldResponseDto> fields;
}
