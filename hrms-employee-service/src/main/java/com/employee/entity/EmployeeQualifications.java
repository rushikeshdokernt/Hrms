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
@Table(name = "employee_qualifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_date IS NULL")
public class EmployeeQualifications extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "qualification_id", nullable = false, updatable = false)
    private UUID qualificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private EmployeeMaster employeeMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "educational_level_id")
    private EducationalLevelMaster educationalLevelMaster;

    @Column(name = "degree", length = 255)
    private String degree;

    @Column(name = "institution_name", length = 255)
    private String institutionName;

    @Column(name = "university_board", length = 500)
    private String universityBoard;

    @Column(name = "year_of_passing")
    private Integer yearOfPassing;

    @Column(name = "score_percentage_cgpa", length = 255)
    private String scorePercentageCgpa;

    @Column(name = "specialization", length = 255)
    private String specialization;

    @Column(name = "grade", length = 255)
    private String grade;

    @Column(name = "education_file_path", length = 255)
    private String educationFilePath;

    @Column(name = "education_file_name", length = 500)
    private String educationFileName;

    @Column(name = "education_doc_type", length = 500)
    private String educationDocType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "addition_detail", columnDefinition = "jsonb")
    private JsonNode additionDetail;
}