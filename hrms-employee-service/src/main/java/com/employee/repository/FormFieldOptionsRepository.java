package com.employee.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FormFieldOptions;
import com.employee.entity.FormMaster;

public interface FormFieldOptionsRepository extends JpaRepository<FormFieldOptions, UUID>{

	 List<FormFieldOptions> findByFormFieldMasterFieldId(UUID fieldId);

}
