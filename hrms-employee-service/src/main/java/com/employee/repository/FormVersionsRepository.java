package com.employee.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FormVersions;

public interface FormVersionsRepository extends JpaRepository<FormVersions, UUID> {

	Optional<FormVersions> findByFormMasterFormId(UUID formId);

	Optional<FormVersions> findByFormMasterFormName(String formName);

	

}
