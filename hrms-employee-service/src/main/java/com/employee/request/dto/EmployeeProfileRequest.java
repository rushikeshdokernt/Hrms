package com.employee.request.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeProfileRequest {

   
    private PersonalInfoDto personalInfo;

   
     
    private EmergencyInfoDto emergencyInfo;

   
    private List<WorkExperienceDto> workExperiences;

  
    private List<EducationDto> educations;

   
    private BankDetailsDto bankDetails;


    private List<FamilyDetailsDto> familyDetails;

   
    private List<NomineeDetailsDto> nominees;

 
    private SocialMediaDto socialMedia;

 
    private PoliceVerificationDto policeVerification;


   

    @Data
    public static class PersonalInfoDto {

        private String firstName;
        private String middleName;
        private String lastName;

        private String phone;
        private String email;

        private LocalDate dateOfBirth;

        private String gender;
        private String maritalStatus;
        private String bloodGroup;
        private String nationality;

        private String aadhaarNumber;
        private String panNumber;

        private String permanentAddress;

        private MultipartFile profilePhoto;
        private MultipartFile aadhaarCard;
        private MultipartFile panCard;
        private MultipartFile addressProof;
    }


    // =========================================================
    // EMERGENCY INFORMATION
    // =========================================================

    @Data
    public static class EmergencyInfoDto {

        private String contactNo1;
        private String contactName1;
        private String relation1;
        private String bloodGroup1;

        private String contactNo2;
        private String contactName2;
        private String relation2;
        private String bloodGroup2;

        private String insuranceType;
        private String policyNumber;
    }


   

    @Data
    public static class WorkExperienceDto {

        private String companyName;
        private String designation;

        private LocalDate fromDate;
        private LocalDate toDate;

        private String totalExperience;

        private String reportingManagerName;
        private String reportingManagerNumber;

        private MultipartFile salarySlip;
        private MultipartFile experienceLetter;
        private MultipartFile appointmentLetter;
        private MultipartFile relievingLetter;
    }



    @Data
    public static class EducationDto {

        private String qualificationType;
        private String courseStream;

        private String institutionSchoolCollege;
        private String boardUniversity;

        private String yearOfPassing;

        private String percentageCgpa;
        private String gradeDivision;

        private String specialization;

        private MultipartFile document;
    }



    @Data
    public static class BankDetailsDto {

        private String ifscCode;
        private String bankName;
        private String branch;

        private String accountType;

        private String accountHolderName;

        private String accountNumber;
        private String confirmAccountNumber;

        private String accountHolderType;
    }



    @Data
    public static class FamilyDetailsDto {

        private String name;
        private String relationship;

        private LocalDate dateOfBirth;

        private String occupation;

        private MultipartFile aadhaarCard;
        private MultipartFile panCard;
    }



    @Data
    public static class NomineeDetailsDto {

        private String nomineeName;

        private Double nomineePercentage;

        private String relationship;

        private String contactNumber;

        private LocalDate dateOfBirth;

        private String address;

        private String idProofType;
        private String idProofNumber;
    }



    @Data
    public static class SocialMediaDto {

        private String whatsAppNumber;

        private String instagramId;

        private String facebookId;

        private String youtubeAccount;
    }



    @Data
    public static class PoliceVerificationDto {

        private String verificationStatus;

        private LocalDate verificationDate;

        private MultipartFile document;
    }
}
