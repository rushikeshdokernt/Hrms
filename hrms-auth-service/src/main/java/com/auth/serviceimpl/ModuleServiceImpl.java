package com.auth.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.auth.dto.response.ApiResponseDto;
import com.auth.dto.response.ModuleListResponse;
import com.auth.dto.response.ModuleUsecasesListResponse;
import com.auth.entity.ModuleMaster;
import com.auth.entity.UseCase;
import com.auth.exception.ResourceNotFoundException;
import com.auth.mapper.ModuleMapper;
import com.auth.mapper.ModuleUsecasesMapper;
import com.auth.repository.ModuleRepository;
import com.auth.repository.UseCaseRepository;
import com.auth.service.ModuleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService{
	
	private final ModuleRepository moduleRepository;
	
	private final ModuleMapper moduleMapper;
	
	private final UseCaseRepository useCaseRepository;
	
	private final ModuleUsecasesMapper moduleUsecasesMapper;

	@Override
	public ResponseEntity<ApiResponseDto> getActiveModules() {

	    List<ModuleMaster> allActiveModules =
	            moduleRepository.findAllByIsActiveTrue();

	    List<ModuleListResponse.ModuleResponse> modules =
	            moduleMapper.toModuleResponseList(allActiveModules);

	    ModuleListResponse response = new ModuleListResponse();
	    response.setModules(modules);
	    
	    ApiResponseDto apiResponseDto=ApiResponseDto.builder()
	    		.success(true)
	    		.message("Modules list loaded successfully")
	    		.data(response)
	    		.build();

	    return ResponseEntity.ok(
	    		apiResponseDto
	    );
	}

	@Override
	public ResponseEntity<ApiResponseDto> getMuduleUsecase() {

	    List<ModuleMaster> allActiveModules =
	            moduleRepository.findAllByIsActiveTrue();

	    List<UseCase> allUseCases =
	            useCaseRepository.findAllByModuleIn(allActiveModules);

	    Map<UUID, List<UseCase>> useCasesByModule =
	            allUseCases.stream()
	                    .collect(Collectors.groupingBy(
	                            useCase -> useCase.getModule().getModuleId()
	                    ));

	    List<ModuleUsecasesListResponse.ModuleResponse> modules =
	            allActiveModules.stream()
	                    .map(module -> {

	                        ModuleUsecasesListResponse.ModuleResponse response =
	                                moduleUsecasesMapper.toModuleResponse(module);

	                        List<UseCase> useCases =
	                                useCasesByModule.getOrDefault(
	                                        module.getModuleId(),
	                                        Collections.emptyList()
	                                );

	                        response.setUseCases(
	                                moduleUsecasesMapper.toUseCaseResponseList(
	                                        useCases
	                                )
	                        );

	                        return response;
	                    })
	                    .toList();

	    ModuleUsecasesListResponse response =
	            new ModuleUsecasesListResponse();

	    response.setModules(modules);

	    ApiResponseDto apiResponseDto = ApiResponseDto.builder()
	            .success(true)
	            .message("Module Usecase List fetched successfully")
	            .data(response)
	            .build();

	    return ResponseEntity.ok(apiResponseDto);
	}

	@Override
	public ResponseEntity<ApiResponseDto> getMuduleWiseUsecase(UUID moduleId) {

	    ModuleMaster module = moduleRepository
	            .findByModuleIdAndIsActiveTrue(moduleId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "No active module exists with id: " + moduleId
	                    )
	            );

	    List<UseCase> useCases =
	            useCaseRepository.findAllByModule(module);

	    ModuleUsecasesListResponse.ModuleResponse moduleResponse =
	            moduleUsecasesMapper.toModuleResponse(module);

	    moduleResponse.setUseCases(
	            moduleUsecasesMapper.toUseCaseResponseList(useCases)
	    );

	    ModuleUsecasesListResponse response =
	            new ModuleUsecasesListResponse();

	    response.setModules(List.of(moduleResponse));

	    ApiResponseDto apiResponseDto = ApiResponseDto.builder()
	            .success(true)
	            .message("Module Usecase List fetched successfully")
	            .data(response)
	            .build();

	    return ResponseEntity.ok(apiResponseDto);
	}
	

}
