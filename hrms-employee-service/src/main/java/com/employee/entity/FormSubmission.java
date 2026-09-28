package com.employee.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.employee.enums.FormSubmissionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class FormSubmission  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID formSubmissionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeMaster employeeMaster;

    @Enumerated(EnumType.STRING)
    @Column(name = "form_submission_status", nullable = false, length = 50)
    private FormSubmissionStatus formSubmissionStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "form_version_id", nullable = false)
    private FormVersions formVersions;
}
