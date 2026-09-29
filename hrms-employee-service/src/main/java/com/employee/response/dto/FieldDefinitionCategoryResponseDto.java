package com.employee.response.dto;


import java.util.List;
import java.util.UUID;

import com.employee.enums.DataType;
import com.employee.enums.FieldDefinitionCategory;
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
public class FieldDefinitionCategoryResponseDto {

    private FieldDefinitionCategory category;

    private List<FieldDefinitionMasterResponseDto> fields;
}