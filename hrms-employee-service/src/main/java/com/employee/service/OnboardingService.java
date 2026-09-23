package com.employee.service;

import org.springframework.http.ResponseEntity;

import com.employee.response.dto.ApiResponseDto;

public interface OnboardingService {

	ResponseEntity<ApiResponseDto>  fetchForm(String formName);

}
