package com.auth.service;

import org.springframework.http.ResponseEntity;

import com.auth.dto.request.DynamicFormRequest;
import com.auth.dto.response.ApiResponseDto;

public interface DynamicFormService {

	ResponseEntity<ApiResponseDto> addDynamicForm(DynamicFormRequest dynamicFormRequest);

	ResponseEntity<ApiResponseDto> getDynamicForm(String formType);

}
