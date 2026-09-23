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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FormFieldResponseDto {

    private UUID fieldId;
    private String fieldKey;
    private String label;
    private String fieldType;
    private String placeholder;
    private String defaultValue;
    private Boolean required;
    private Boolean disabled;
    private Boolean readonly;
    private Boolean visible;
    private Boolean validation;
    private Integer sortOrder;
    private Boolean immutable;
    private Boolean masterData;

    private List<FormFieldOptionResponseDto> options;
    private List<FormFieldValidationResponseDto> validations;
}
