package com.employee.serviceimpl;

import static com.employee.constant.ResponseMessageConstant.FORM_TYPES_FETCHED_SUCCESSFULLY;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.constant.ResponseMessageConstant;
import com.employee.entity.FormFieldMaster;
import com.employee.entity.FormMaster;
import com.employee.entity.FormSectionMaster;
import com.employee.entity.FormVersions;
import com.employee.enums.FieldType;
import com.employee.exception.ResourceNotFoundException;
import com.employee.mapper.FormMasterMapper;
import com.employee.mapper.FormSectionMapper;
import com.employee.repository.FormFieldMasterRepository;
import com.employee.repository.FormFieldOptionsRepository;
import com.employee.repository.FormMasterRepository;
import com.employee.repository.FormSectionMasterRepository;
import com.employee.repository.FormVersionsRepository;
import com.employee.request.dto.AddFormSectionRequestDto;
import com.employee.request.dto.UpdateFormSectionRequestDto;
import com.employee.response.dto.ApiResponseDto;
import com.employee.response.dto.FormFieldOptionResponseDto;
import com.employee.response.dto.FormFieldResponseDto;
import com.employee.response.dto.FormResponseDto;
import com.employee.response.dto.FormSectionResponseDto;
import com.employee.service.AdminConfigFormFieldService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdminConfigFormFieldServiceImpl implements AdminConfigFormFieldService {

	private final FormMasterRepository formMasterRepository;
	private final FormSectionMasterRepository formSectionMasterRepository;
	private final FormFieldMasterRepository formFieldMasterRepository;
	private final FormFieldOptionsRepository formFieldOptionsRepository;

	private final FormMasterMapper formMasterMapper;
	private final FormSectionMapper formSectionMapper;
	
	private final FormVersionsRepository formVersionsRepository;

	@Override
	public ResponseEntity<ApiResponseDto> getFormTypes() {

		List<FormMaster> forms = formMasterRepository.findAll();

		List<FormResponseDto> response = formMasterMapper.toResponseDtoList(forms);

		ApiResponseDto apiResponse = ApiResponseDto.builder().success(true).message(FORM_TYPES_FETCHED_SUCCESSFULLY)
				.data(response).build();

		return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
	}

	@Override
	public ResponseEntity<ApiResponseDto> fetchForm(String formName) {
		
		FormVersions formVersions = formVersionsRepository
		        .findByFormMasterFormName(formName)
		        .orElseThrow(() -> new ResourceNotFoundException(
		                "Form version not found for form name: " + formName
		        ));
		// 2. Fetch sections
		List<FormSectionMaster> formSectionList = formSectionMasterRepository
				.findByFormVersionsFormVersionId(formVersions.getFormVersionId());

		// 3. Map sections
		List<FormSectionResponseDto> sectionResponseList = formSectionList.stream().map(this::mapSection).toList();

		// 4. Build form response
		FormResponseDto response = FormResponseDto.builder().formId(formVersions.getFormMaster().getFormId())
				.formName(formVersions.getFormMaster().getFormName()).build();

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

		if (field.getFieldType().equals(FieldType.DROPDOWN)) {

			optionResponseList = formFieldOptionsRepository.findByFormFieldMasterFieldId(field.getFieldId()).stream()
					.map(option -> FormFieldOptionResponseDto.builder().fieldOptionId(option.getFieldOptionId())
							.optionKey(option.getOptionKey()).optionValue(option.getOptionValue())
							.sortOrder(option.getSortOrder()).build())
					.toList();
		}

		JsonNode validationConfig = field.getValidationConfig();

		return FormFieldResponseDto.builder().fieldId(field.getFieldId()).fieldKey(field.getFieldKey())
				.label(field.getLabel()).fieldType(field.getFieldType().name()).placeholder(field.getPlaceholder())
				.required(field.getIsRequired()).disabled(field.getDisabled()).readonly(field.getReadonly())
				.visible(field.getVisible()).sortOrder(field.getSortOrder()).options(optionResponseList).build();
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> addFormSection(HttpServletRequest request,
			AddFormSectionRequestDto addFormSectionRequestDto) {

		UUID formId = addFormSectionRequestDto.getFormId();

		FormMaster formMaster = formMasterRepository.findById(formId)
				.orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));

		FormSectionMaster formSectionMaster = formSectionMapper.toEntity(addFormSectionRequestDto);

		FormVersions formVersions = formVersionsRepository.findByFormMasterFormId(formId)
				.orElseThrow(()-> new ResourceNotFoundException("Form version not found for form id: " + formId));
		
		formVersions.setFormMaster(formMaster);
		formSectionMaster.setFormVersions(formVersions);

		FormSectionMaster savedSection = formSectionMasterRepository.save(formSectionMaster);

		FormSectionResponseDto response = formSectionMapper.toResponse(savedSection);

		ApiResponseDto apiResponse = ApiResponseDto.builder().success(true)
				.message(ResponseMessageConstant.FORM_SECTION_CREATED_SUCCESSFULLY).data(response).build();

		return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> updateFormSection(HttpServletRequest request,
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

		return ResponseEntity.ok(ApiResponseDto.builder().success(true)
				.message(ResponseMessageConstant.FORM_SECTION_UPDATED_SUCCESSFULLY).data(response).build());
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> deleteFormSection(HttpServletRequest request, UUID formSectionId) {

		// 1. Fetch existing section (SQLRestriction ensures it is not already deleted)
		FormSectionMaster section = formSectionMasterRepository.findById(formSectionId)
				.orElseThrow(() -> new ResourceNotFoundException("Form section not found with id: " + formSectionId));

		// 2. Soft delete — set deletedDate; @SQLRestriction will hide it going forward
		section.setDeletedDate(java.time.OffsetDateTime.now());
		section.setDeletedBy(request.getHeader("X-User") != null ? request.getHeader("X-User") : "system");

		formSectionMasterRepository.save(section);

		return ResponseEntity.ok(ApiResponseDto.builder().success(true)
				.message(ResponseMessageConstant.FORM_SECTION_DELETED_SUCCESSFULLY).build());
	}

}
