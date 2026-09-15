package com.auth.entity;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class DynamicFormStructure extends Auditable{

	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "form_id", nullable = false)
    private UUID formId;

    @Column(name = "form_type", unique = true, nullable = false)
    private String formType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "form_fields", columnDefinition = "jsonb", nullable = false)
    private JsonNode formFields;
}
