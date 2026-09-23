package com.employee.response.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FormFieldOptionResponseDto {

    private UUID fieldOptionId;
    private String optionLabel;
    private String optionValue;
    private Integer sortOrder;
}