package com.employee.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "form_field_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_date IS NULL")
public class FormFieldMaster extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "field_id", nullable = false, updatable = false)
    private UUID fieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_section_id")
    private FormSectionMaster formSectionMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id")
    private DocumentTypeMaster documentTypeMaster;

    @Column(name = "field_key", length = 500)
    private String fieldKey;

    @Column(name = "label", length = 500)
    private String label;

    @Column(name = "field_type", length = 30)
    private String fieldType;

    @Column(name = "placeholder", length = 255)
    private String placeholder;

    @Column(name = "default_value", length = 100)
    private String defaultValue;

    @Column(name = "is_required", nullable = false)
    private Boolean required;

    @Column(name = "is_disabled", nullable = false)
    private Boolean disabled;

    @Column(name = "is_readonly", nullable = false)
    private Boolean readonly;

    @Column(name = "is_visible", nullable = false)
    private Boolean visible;

    @Column(name = "is_validation", nullable = false)
    private Boolean validation;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "addition_detail", columnDefinition = "jsonb")
    private JsonNode additionDetail;

    @Column(name = "is_immutable", nullable = false)
    private Boolean immutable;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;
}