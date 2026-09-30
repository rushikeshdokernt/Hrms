package com.employee.request.dto;

import java.util.UUID;

import com.employee.enums.DataType;
import com.employee.enums.FieldType;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotNull;
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
public class UpdateFormFieldDto {

    /**
     * ID of the existing FormFieldMaster record to update.
     */
    @NotNull(message = "Field ID is required")
    private UUID fieldId;

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
