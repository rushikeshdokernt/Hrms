package com.employee.service;

import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.employee.request.dto.AddFormSectionRequestDto;
import com.employee.request.dto.CreateFormDto;
import com.employee.request.dto.UpdateFormRequestDto;
import com.employee.request.dto.UpdateFormSectionRequestDto;
import com.employee.response.dto.ApiResponseDto;

import jakarta.servlet.http.HttpServletRequest;

public interface AdminConfigFormFieldService {

	ResponseEntity<ApiResponseDto> fetchForm(String formName);

	ResponseEntity<ApiResponseDto> getFormTypes();

	ResponseEntity<ApiResponseDto> addFormSection(HttpServletRequest request,
			AddFormSectionRequestDto addFormSectionRequestDto);

	ResponseEntity<ApiResponseDto> updateFormSection(HttpServletRequest request,
			UpdateFormSectionRequestDto requestDto);

	ResponseEntity<ApiResponseDto> deleteFormSection(HttpServletRequest request, UUID formSectionId);

	ResponseEntity<ApiResponseDto> getFormDefination();

	ResponseEntity<ApiResponseDto> createFormFields(CreateFormDto createFormDto, HttpServletRequest request);

	/**
	 * Updates FormFieldMaster records + creates a new incremented FormVersion entry.
	 */
	ResponseEntity<ApiResponseDto> updateFormFields(UpdateFormRequestDto requestDto, HttpServletRequest request);

	/**
	 * Soft-deletes all FormFieldMaster records for the form + all its FormVersions.
	 */
	ResponseEntity<ApiResponseDto> deleteFormFields(UUID formId, HttpServletRequest request);

}
