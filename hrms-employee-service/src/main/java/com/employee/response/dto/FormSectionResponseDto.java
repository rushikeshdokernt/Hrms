package com.employee.response.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FormSectionResponseDto {

    private UUID formSectionId;
    private String sectionName;
    private Boolean visible;
    private Integer sortOrder;
    private Boolean immutable;
    private List<FormFieldResponseDto> fields;
}
