package com.employee.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FormFieldValidations;
import com.employee.entity.FormMaster;

public interface FormFieldValidationsRepository extends JpaRepository<FormFieldValidations, UUID>{

	  List<FormFieldValidations> findByFormFieldMasterFieldId(UUID fieldId);

}
