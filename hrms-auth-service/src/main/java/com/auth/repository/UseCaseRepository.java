package com.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.ModuleMaster;
import com.auth.entity.UseCase;

public interface UseCaseRepository extends JpaRepository<UseCase, UUID>{

	List<UseCase> findAllByModuleIn(List<ModuleMaster> allActiveModules);

	List<UseCase> findAllByModule(ModuleMaster module);

}
