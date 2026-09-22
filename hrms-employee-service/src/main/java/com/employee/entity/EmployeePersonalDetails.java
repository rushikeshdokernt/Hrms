package com.employee.entity;

import java.time.LocalDate;
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
@Table(name = "employee_personal_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_date IS NULL")
public class EmployeePersonalDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "personal_id", nullable = false, updatable = false)
    private UUID personalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private EmployeeMaster employeeMaster;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 255)
    private String gender;

    @Column(name = "marital_status", length = 255)
    private String maritalStatus;

    @Column(name = "nationality", length = 255)
    private String nationality;

    @Column(name = "blood_group", length = 255)
    private String bloodGroup;

    @Column(name = "personal_phone_number", length = 255)
    private String personalPhoneNumber;

    @Column(name = "personal_email", length = 255)
    private String personalEmail;

    @Column(name = "aadhaar_number", length = 255)
    private String aadhaarNumber;

    @Column(name = "pan_number", length = 255)
    private String panNumber;

    @Column(name = "permanent_address", length = 255)
    private String permanentAddress;

    @Column(name = "current_address", length = 255)
    private String currentAddress;

    @Column(name = "emergency_contact_number", length = 255)
    private String emergencyContactNumber;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "addition_detail", columnDefinition = "jsonb")
    private JsonNode additionDetail;
}