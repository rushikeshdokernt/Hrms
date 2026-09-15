package com.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.request.DynamicFormRequest;
import com.auth.dto.response.ApiResponseDto;
import com.auth.service.DynamicFormService;

import lombok.RequiredArgsConstructor;

import static com.auth.constant.ApiConstants.FORM;
import static com.auth.constant.ApiConstants.ADD;

@RestController
@RequestMapping(FORM)
@RequiredArgsConstructor
public class DynamicFormController {
	
	private final DynamicFormService dynamicFormService;

	@PostMapping
	public ResponseEntity<ApiResponseDto> addDynamicForm(@RequestBody DynamicFormRequest dynamicFormRequest){
		return dynamicFormService.addDynamicForm(dynamicFormRequest);
	}
	
	@GetMapping("/{formType}")
	public ResponseEntity<ApiResponseDto> getDynamicForm(@PathVariable String formType){
		return dynamicFormService.getDynamicForm(formType);
	}
}
