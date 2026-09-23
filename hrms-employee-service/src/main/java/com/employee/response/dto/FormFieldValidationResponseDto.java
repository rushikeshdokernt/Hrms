package com.employee.response.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FormFieldValidationResponseDto {

    private UUID fieldValidationId;
    private String validationType;
    private String validationValue;
    private String errorMessage;
    private Integer sortOrder;
}