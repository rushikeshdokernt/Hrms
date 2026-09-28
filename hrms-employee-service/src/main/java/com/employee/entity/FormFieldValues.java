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
@Table(name = "employee_form_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_date IS NULL")
public class FormFieldValues extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "form_filed_values_id", nullable = false, updatable = false)
    private UUID formFiledValuesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_submission_id")
    private FormSubmission formSubmission;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id")
    private FormFieldMaster formFieldMaster;

    @Column(name = "field_data", columnDefinition = "TEXT")
    private String formFieldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "addition_detail", columnDefinition = "jsonb")
    private JsonNode additionDetail;
}