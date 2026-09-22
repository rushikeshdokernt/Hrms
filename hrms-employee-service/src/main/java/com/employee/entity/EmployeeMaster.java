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
@Table(name = "employee_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_date IS NULL")
public class EmployeeMaster extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employee_id", nullable = false, updatable = false)
    private UUID employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationMaster locationMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentMaster departmentMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private EmployeeMaster reportingManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private DesignationMaster designationMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private StateMaster stateMaster;

    @Column(name = "first_name", length = 500)
    private String firstName;

    @Column(name = "middle_name", length = 500)
    private String middleName;

    @Column(name = "last_name", length = 500)
    private String lastName;

    @Column(name = "hrms_employee_id", length = 255)
    private String hrmsEmployeeId;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "employee_type", length = 255)
    private String employeeType;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "official_email_id", length = 500, unique = true)
    private String officialEmailId;

    @Column(name = "official_mobile_no", length = 20)
    private String officialMobileNo;

    @Column(name = "desk_number", length = 255)
    private String deskNumber;

    @Column(name = "is_important_employee")
    private Boolean importantEmployee;

    @Column(name = "status", length = 20)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "addition_detail", columnDefinition = "jsonb")
    private JsonNode additionDetail;

    @Column(name = "onboarding_status", length = 50)
    private String onboardingStatus;
}