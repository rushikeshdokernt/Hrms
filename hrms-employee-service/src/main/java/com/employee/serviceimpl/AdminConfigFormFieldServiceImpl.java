package com.employee.serviceimpl;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.constant.ResponseMessageConstant;
import com.employee.entity.FormFieldMaster;
import com.employee.entity.FormMaster;
import com.employee.entity.FormSectionMaster;
import com.employee.exception.ResourceNotFoundException;
import com.employee.mapper.FormMasterMapper;
import com.employee.mapper.FormSectionMapper;
import com.employee.repository.FormFieldMasterRepository;
import com.employee.repository.FormFieldOptionsRepository;
import com.employee.repository.FormFieldValidationsRepository;
import com.employee.repository.FormMasterRepository;
import com.employee.repository.FormSectionMasterRepository;
import com.employee.request.dto.AddFormSectionRequestDto;
import com.employee.request.dto.UpdateFormSectionRequestDto;
import com.employee.response.dto.ApiResponseDto;
import com.employee.response.dto.FormFieldOptionResponseDto;
import com.employee.response.dto.FormFieldResponseDto;
import com.employee.response.dto.FormFieldValidationResponseDto;
import com.employee.response.dto.FormResponseDto;
import com.employee.response.dto.FormSectionResponseDto;
import com.employee.service.AdminConfigFormFieldService;

import jakarta.servlet.http.HttpServletRequest;

import static com.employee.constant.ResponseMessageConstant.FORM_TYPES_FETCHED_SUCCESSFULLY;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminConfigFormFieldServiceImpl implements AdminConfigFormFieldService {

	private final FormMasterRepository formMasterRepository;
	private final FormSectionMasterRepository formSectionMasterRepository;
	private final FormFieldMasterRepository formFieldMasterRepository;
	private final FormFieldOptionsRepository formFieldOptionsRepository;
	private final FormFieldValidationsRepository formFieldValidationsRepository;

	private final FormMasterMapper formMasterMapper;
	private final FormSectionMapper formSectionMapper;

	@Override
	public ResponseEntity<ApiResponseDto> getFormTypes() {

		List<FormMaster> forms = formMasterRepository.findAllByOrderBySortOrderAsc();

		List<FormResponseDto> response = formMasterMapper.toResponseDtoList(forms);

		ApiResponseDto apiResponse = ApiResponseDto.builder().success(true).message(FORM_TYPES_FETCHED_SUCCESSFULLY)
				.data(response).build();

		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@Override
	public ResponseEntity<ApiResponseDto> fetchForm(String formName) {

		// 1. Fetch form
		FormMaster formMaster = formMasterRepository.findByFormName(formName)
				.orElseThrow(() -> new ResourceNotFoundException("Form not exists with name " + formName));

		// 2. Fetch sections
		List<FormSectionMaster> formSectionList = formSectionMasterRepository
				.findByFormMasterFormId(formMaster.getFormId());

		// 3. Map sections
		List<FormSectionResponseDto> sectionResponseList = formSectionList.stream().map(this::mapSection).toList();

		// 4. Build form response
		FormResponseDto response = FormResponseDto.builder().formId(formMaster.getFormId())
				.formName(formMaster.getFormName()).visible(formMaster.getVisible())
				.sortOrder(formMaster.getSortOrder()).sections(sectionResponseList).build();

		// 5. Return response
		return ResponseEntity
				.ok(ApiResponseDto.builder().success(true).message("Form fetched successfully").data(response).build());
	}

	private FormSectionResponseDto mapSection(FormSectionMaster section) {

		List<FormFieldMaster> fieldList = formFieldMasterRepository
				.findByFormSectionMasterFormSectionId(section.getFormSectionId());

		List<FormFieldResponseDto> fieldResponseList = fieldList.stream().map(this::mapField).toList();

		return FormSectionResponseDto.builder().formSectionId(section.getFormSectionId())
				.sectionName(section.getSectionName()).visible(section.getVisible()).sortOrder(section.getSortOrder())
				.immutable(section.getImmutable()).fields(fieldResponseList).build();
	}

	private FormFieldResponseDto mapField(FormFieldMaster field) {

		List<FormFieldOptionResponseDto> optionResponseList = List.of();

		if ("DROPDOWN".equalsIgnoreCase(field.getFieldType())) {

			optionResponseList = formFieldOptionsRepository.findByFormFieldMasterFieldId(field.getFieldId()).stream()
					.map(option -> FormFieldOptionResponseDto.builder().fieldOptionId(option.getFieldOptionId())
							.optionLabel(option.getOptionLabel()).optionValue(option.getOptionValue())
							.sortOrder(option.getSortOrder()).build())
					.toList();
		}

		List<FormFieldValidationResponseDto> validationResponseList = formFieldValidationsRepository
				.findByFormFieldMasterFieldId(field.getFieldId()).stream()
				.map(validation -> FormFieldValidationResponseDto.builder()
						.fieldValidationId(validation.getFieldValidationId())
						.validationType(validation.getValidationType()).validationValue(validation.getValidationValue())
						.errorMessage(validation.getErrorMessage()).sortOrder(validation.getSortOrder()).build())
				.toList();

		return FormFieldResponseDto.builder().fieldId(field.getFieldId()).fieldKey(field.getFieldKey())
				.label(field.getLabel()).fieldType(field.getFieldType()).placeholder(field.getPlaceholder())
				.defaultValue(field.getDefaultValue()).required(field.getRequired()).disabled(field.getDisabled())
				.readonly(field.getReadonly()).visible(field.getVisible()).validation(field.getValidation())
				.sortOrder(field.getSortOrder()).immutable(field.getImmutable()).options(optionResponseList)
				.validations(validationResponseList).build();
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> addFormSection(
	        HttpServletRequest request,
	        AddFormSectionRequestDto addFormSectionRequestDto) {

	    UUID formId = addFormSectionRequestDto.getFormId();

	    FormMaster formMaster = formMasterRepository.findById(formId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Form not found with id: " + formId));

	    FormSectionMaster formSectionMaster =
	            formSectionMapper.toEntity(addFormSectionRequestDto);

	    formSectionMaster.setFormMaster(formMaster);

	    FormSectionMaster savedSection =
	            formSectionMasterRepository.save(formSectionMaster);

	    FormSectionResponseDto response =
	            formSectionMapper.toResponse(savedSection);

	    ApiResponseDto apiResponse = ApiResponseDto.builder()
	            .success(true)
	            .message(ResponseMessageConstant.FORM_SECTION_CREATED_SUCCESSFULLY)
	            .data(response)
	            .build();

	    return ResponseEntity
	            .status(HttpStatus.CREATED)
	            .body(apiResponse);
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> updateFormSection(
	        HttpServletRequest request,
	        UpdateFormSectionRequestDto requestDto) {

	    // 1. Fetch existing section (SQLRestriction filters deleted rows)
	    FormSectionMaster section = formSectionMasterRepository.findById(requestDto.getFormSectionId())
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Form section not found with id: " + requestDto.getFormSectionId()));

	    // 2. Update only section name
	    section.setSectionName(requestDto.getSectionName());

	    // 3. Persist
	    FormSectionMaster updatedSection = formSectionMasterRepository.save(section);

	    // 4. Map to response
	    FormSectionResponseDto response = formSectionMapper.toResponse(updatedSection);

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message(ResponseMessageConstant.FORM_SECTION_UPDATED_SUCCESSFULLY)
	                    .data(response)
	                    .build());
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> deleteFormSection(
	        HttpServletRequest request,
	        UUID formSectionId) {

	    // 1. Fetch existing section (SQLRestriction ensures it is not already deleted)
	    FormSectionMaster section = formSectionMasterRepository.findById(formSectionId)
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Form section not found with id: " + formSectionId));

	    // 2. Soft delete — set deletedDate; @SQLRestriction will hide it going forward
	    section.setDeletedDate(java.time.OffsetDateTime.now());
	    section.setDeletedBy(request.getHeader("X-User") != null
	            ? request.getHeader("X-User") : "system");

	    formSectionMasterRepository.save(section);

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message(ResponseMessageConstant.FORM_SECTION_DELETED_SUCCESSFULLY)
	                    .build());
	}

}
