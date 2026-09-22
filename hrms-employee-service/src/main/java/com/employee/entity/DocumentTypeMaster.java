package com.employee.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_type_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_date IS NULL")
public class DocumentTypeMaster extends Auditable {

    @Id
    @Column(name = "document_type_id", nullable = false, updatable = false)
    private UUID documentTypeId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "addition_detail", columnDefinition = "jsonb")
    private JsonNode additionDetail;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "document_name", length = 100)
    private String documentName;

    @Column(name = "is_required")
    private Boolean required;

    @Column(name = "max_file_size_mb")
    private Integer maxFileSizeMb;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "type_label", length = 255)
    private String typeLabel;
}
