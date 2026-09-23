package com.employee.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FormFieldMaster;

public interface FormFieldMasterRepository extends JpaRepository<FormFieldMaster, UUID>{

	List<FormFieldMaster> findByFormSectionMasterFormSectionId(UUID formSectionId);

}
