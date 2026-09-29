package com.employee.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.FieldDefinitionMaster;

public interface FieldDefinationRepository extends JpaRepository<FieldDefinitionMaster, UUID>{

}
