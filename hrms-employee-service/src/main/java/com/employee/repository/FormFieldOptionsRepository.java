package com.employee.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FormFieldOptions;

public interface FormFieldOptionsRepository extends JpaRepository<FormFieldOptions, UUID>{

	 List<FormFieldOptions> findByFormFieldMasterFieldId(UUID fieldId);

}
