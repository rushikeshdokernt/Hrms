package com.employee.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FormMaster;

public interface FormMasterRepository extends JpaRepository<FormMaster, UUID>{

	Optional<FormMaster> findByFormName(String formName);

}
