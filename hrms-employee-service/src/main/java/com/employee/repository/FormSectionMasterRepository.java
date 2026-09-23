package com.employee.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FormSectionMaster;

public interface FormSectionMasterRepository extends JpaRepository<FormSectionMaster, UUID>{

	List<FormSectionMaster> findByFormMasterFormId(UUID formId);


}
