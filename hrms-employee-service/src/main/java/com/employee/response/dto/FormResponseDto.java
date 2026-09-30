package com.employee.response.dto;


import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormResponseDto {

    private UUID formId;
    private String formName;
    private String formType;
    private String formDescription;
}
