package com.employee.request.dto;

import java.util.UUID;

import com.employee.enums.DataType;
import com.employee.enums.FieldType;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFormFieldDto {

    private UUID fieldDefinitionId;

    private String fieldDescription;

    private DataType dataType;

    private String fieldKey;

    private String label;

    private FieldType fieldType;

    private String placeholder;

    private Boolean disabled;

    private Boolean readonly;

    private Boolean visible;

    private JsonNode validationConfig;

    private Integer sortOrder;

    private Boolean isActive;

    private Boolean isRequired;

    private Boolean isFilterable;

    private JsonNode additionDetail;
}