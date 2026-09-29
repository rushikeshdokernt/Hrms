package com.employee.response.dto;


import java.util.UUID;

import com.employee.enums.DataType;
import com.employee.enums.FieldDefinitionKey;
import com.employee.enums.FieldType;
import com.employee.enums.MappingType;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDefinitionMasterResponseDto {

    private UUID fieldDefinationId;


    private String componentType;

    private DataType dataType;

    private String description;

    private FieldDefinitionKey fieldDefinitionKey;

    private FieldType fieldType;

    private Boolean filterable;

    private String icon;

    private String label;

    private MappingType mappingType;

    private Boolean requiredByDefault;

    private Integer sortOrder;

    private Boolean supportsOptions;

    private JsonNode validationRulesJson;
}
