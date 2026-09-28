package com.employee.controller;

import static com.employee.constant.ApiConstants.EMPLOYEE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.request.dto.EmployeeProfileRequest;
import com.employee.response.dto.ApiResponseDto;
import com.employee.service.OnboardingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(EMPLOYEE)
@RequiredArgsConstructor
public class OnboardingController {
	
	private final OnboardingService onboardingService;

	@PostMapping
	public ResponseEntity<ApiResponseDto> addEmployee(@RequestBody EmployeeProfileRequest employeeProfileRequest){
		return null;
		
	}
	
	
}
