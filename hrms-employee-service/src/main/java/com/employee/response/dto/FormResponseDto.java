package com.employee.response.dto;


import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FormResponseDto {

    private UUID formId;
    private String formName;
    private Boolean visible;
    private Integer sortOrder;
    private List<FormSectionResponseDto> sections;
}
