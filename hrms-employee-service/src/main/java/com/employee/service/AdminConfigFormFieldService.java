package com.employee.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.employee.request.dto.AddFormSectionRequestDto;
import com.employee.request.dto.UpdateFormSectionRequestDto;
import com.employee.response.dto.ApiResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface AdminConfigFormFieldService {
	
	ResponseEntity<ApiResponseDto>  fetchForm(String formName);
	
	ResponseEntity<ApiResponseDto> getFormTypes();

	ResponseEntity<ApiResponseDto> addFormSection(HttpServletRequest request, AddFormSectionRequestDto addFormSectionRequestDto);

	ResponseEntity<ApiResponseDto> updateFormSection(HttpServletRequest request, UpdateFormSectionRequestDto requestDto);

	ResponseEntity<ApiResponseDto> deleteFormSection(HttpServletRequest request, UUID formSectionId);

}
