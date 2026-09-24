package com.employee.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.employee.request.dto.AddFormSectionRequestDto;
import com.employee.request.dto.UpdateFormSectionRequestDto;
import com.employee.response.dto.ApiResponseDto;
import com.employee.service.AdminConfigFormFieldService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import static com.employee.constant.ApiConstants.DELETE_SECTION;
import static com.employee.constant.ApiConstants.FORM;
import static com.employee.constant.ApiConstants.FORM_FIELD;
import static com.employee.constant.ApiConstants.SECTION;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(FORM_FIELD)
@AllArgsConstructor
public class AdminConfigFormFieldController {
	
	private final AdminConfigFormFieldService adminConfigFormFieldService;
	
	
	@GetMapping()
	public ResponseEntity<ApiResponseDto> getFormTypes(){
		
		return adminConfigFormFieldService.getFormTypes();
	}
	
	@GetMapping(FORM)
	public ResponseEntity<ApiResponseDto> fetchForm(@RequestParam String formName){
		return adminConfigFormFieldService.fetchForm(formName);
	}
	
	/**
	 * POST /api/v1/employee/admin-config/form-fields/section
	 * Creates a new form section.
	 */
	@PostMapping(SECTION)
	public ResponseEntity<ApiResponseDto> addFormSection(
			@Valid @RequestBody AddFormSectionRequestDto addFormSectionRequestDto,
			HttpServletRequest request
			){
		return adminConfigFormFieldService.addFormSection(request, addFormSectionRequestDto);
		
	}

	/**
	 * PUT /api/v1/employee/admin-config/form-fields/section/update
	 * Updates section name. formSectionId + sectionName come from request body.
	 */
	@PutMapping(SECTION)
	public ResponseEntity<ApiResponseDto> updateFormSection(
			@Valid @RequestBody UpdateFormSectionRequestDto requestDto,
			HttpServletRequest request) {

		return adminConfigFormFieldService.updateFormSection(request, requestDto);
	}

	/**
	 * DELETE /api/v1/employee/admin-config/form-fields/section/delete/{formSectionId}
	 * Soft-deletes a form section (sets deleted_date; filtered by @SQLRestriction).
	 */
	@DeleteMapping(DELETE_SECTION)
	public ResponseEntity<ApiResponseDto> deleteFormSection(
			@PathVariable UUID formSectionId,
			HttpServletRequest request) {

		return adminConfigFormFieldService.deleteFormSection(request, formSectionId);
	}

}
