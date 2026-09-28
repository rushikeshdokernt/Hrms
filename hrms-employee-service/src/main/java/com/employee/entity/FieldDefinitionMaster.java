package com.employee.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.employee.enums.DataType;
import com.employee.enums.FieldDefinitionCategory;
import com.employee.enums.FieldDefinitionKey;
import com.employee.enums.FieldType;
import com.employee.enums.MappingType;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDefinitionMaster extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID fieldDefinationId;

    @Column(name = "field_defination_category", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private FieldDefinitionCategory fieldDefinitionCategory;

    @Column(name = "component_type", nullable = false, length = 100)
    private String componentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 20)
    private DataType dataType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_defination_key", nullable = false, length = 30)
    private FieldDefinitionKey fieldDefinitionKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 30)
    private FieldType fieldType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "filter_operators_json", columnDefinition = "jsonb")
    private JsonNode filterOperatorsJson;

    @Column(name = "filterable", nullable = false)
    private Boolean filterable;

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "label", nullable = false, length = 150)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "mapping_type", length = 20)
    private MappingType mappingType;

    @Column(name = "required_by_default", nullable = false)
    private Boolean requiredByDefault;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "supports_options", nullable = false)
    private Boolean supportsOptions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "validation_rules_json", columnDefinition = "jsonb")
    private JsonNode validationRulesJson;
}
