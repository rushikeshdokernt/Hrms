package com.auth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.DynamicFormStructure;

public interface DynamicFormRepository extends JpaRepository<DynamicFormStructure, UUID>{

	boolean existsByFormType(String formType);

	DynamicFormStructure findByFormType(String formType);

	DynamicFormStructure findByFormTypeIgnoreCase(String formType);

}
