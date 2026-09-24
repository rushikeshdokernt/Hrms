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
@Table(name = "form_section_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_date IS NULL")
public class FormSectionMaster extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "form_section_id", nullable = false, updatable = false)
    private UUID formSectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id")
    private FormMaster formMaster;

    @Column(name = "section_name", length = 1000)
    private String sectionName;

    @Builder.Default
    @Column(name = "is_visible", nullable = false)
    private Boolean visible = true;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "addition_detail", columnDefinition = "jsonb")
    private JsonNode additionDetail;

    @Builder.Default
    @Column(name = "is_immutable", nullable = false)
    private Boolean immutable = true;
}