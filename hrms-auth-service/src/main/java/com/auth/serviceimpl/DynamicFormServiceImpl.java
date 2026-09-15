package com.auth.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.auth.dto.request.DynamicFormRequest;
import com.auth.dto.response.ApiResponseDto;
import com.auth.dto.response.DynamicFormResponse;
import com.auth.entity.DynamicFormStructure;
import com.auth.exception.DuplicateResourceException;
import com.auth.exception.ResourceNotFoundException;
import com.auth.mapper.DynamicFormMapper;
import com.auth.repository.DynamicFormRepository;
import com.auth.service.DynamicFormService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DynamicFormServiceImpl implements DynamicFormService{
	
	private final DynamicFormRepository dynamicFormRepository;
	
	private final DynamicFormMapper dynamicFormMapper;

	
	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> addDynamicForm(
	        DynamicFormRequest dynamicFormRequest) {

	    if (dynamicFormRepository.existsByFormType(
	            dynamicFormRequest.getFormType())) {

	        throw new DuplicateResourceException(
	                "Form type already exists: "
	                        + dynamicFormRequest.getFormType());
	    }

	    DynamicFormStructure dynamicFormStructure =
	            dynamicFormMapper.toEntity(dynamicFormRequest);

	    dynamicFormRepository.save(dynamicFormStructure);

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(ApiResponseDto.builder()
	                    .success(true)
	                    .message("Dynamic form created successfully")
	                    .build());
	}


	@Override
	public ResponseEntity<ApiResponseDto> getDynamicForm(String formType) {

	    DynamicFormStructure dynamicFormStructure =
	            dynamicFormRepository.findByFormType(formType);

	    if (dynamicFormStructure == null) {
	        throw new ResourceNotFoundException(
	                "Dynamic form not found for form type: " + formType);
	    }

	    DynamicFormResponse responseDto =
	            dynamicFormMapper.toResponseDto(dynamicFormStructure);

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message("Dynamic form fetched successfully")
	                    .data(responseDto)
	                    .build()
	    );
	}

	


}
