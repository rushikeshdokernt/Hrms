package com.auth.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.auth.dto.response.ApiResponseDto;

public interface ModuleService {

	ResponseEntity<ApiResponseDto> getActiveModules();

	ResponseEntity<ApiResponseDto> getMuduleUsecase();

	ResponseEntity<ApiResponseDto> getMuduleWiseUsecase(UUID moduleId);

}
