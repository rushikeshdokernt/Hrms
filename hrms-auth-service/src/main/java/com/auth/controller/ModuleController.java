package com.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.response.ApiResponseDto;
import com.auth.service.ModuleService;

import lombok.RequiredArgsConstructor;

import static com.auth.constant.ApiConstants.MODULE;
import static com.auth.constant.ApiConstants.USECASES;

import java.util.UUID;

@RestController
@RequestMapping(MODULE)
@RequiredArgsConstructor
public class ModuleController {
	
	private final ModuleService moduleService;

	@GetMapping
	public ResponseEntity<ApiResponseDto> getActiveModules(){
		return moduleService.getActiveModules();
	}
	
	@GetMapping(USECASES)
	public ResponseEntity<ApiResponseDto> getMuduleUsecase(){
		return moduleService.getMuduleUsecase();
	}
	
	@GetMapping(USECASES+"/{moduleId}")
	public ResponseEntity<ApiResponseDto> getMuduleWiseUsecase(@PathVariable UUID moduleId){
		return moduleService.getMuduleWiseUsecase(moduleId);
	}
	
	
}
