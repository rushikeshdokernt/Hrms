package com.auth.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.ModuleMaster;

public interface ModuleRepository extends JpaRepository<ModuleMaster, UUID>{

	List<ModuleMaster> findAllByIsActiveTrue();

	Optional<ModuleMaster> findByModuleIdAndIsActiveTrue(UUID moduleId);

}
