package com.employee.serviceimpl;

import static com.employee.constant.ResponseMessageConstant.FORM_TYPES_FETCHED_SUCCESSFULLY;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.constant.ResponseMessageConstant;
import com.employee.entity.FieldDefinitionMaster;
import com.employee.entity.FormFieldMaster;
import com.employee.entity.FormMaster;
import com.employee.entity.FormSectionMaster;
import com.employee.entity.FormVersions;
import com.employee.enums.FieldDefinitionCategory;
import com.employee.enums.FieldType;
import com.employee.enums.FormStatus;
import com.employee.exception.BadRequestException;
import com.employee.exception.ResourceNotFoundException;
import com.employee.mapper.FieldDefinitionMasterMapper;
import com.employee.mapper.FormMasterMapper;
import com.employee.mapper.FormSectionMapper;
import com.employee.mapper.FormFieldMapper;
import com.employee.repository.FieldDefinitionMasterRepository;
import com.employee.repository.FormFieldMasterRepository;
import com.employee.repository.FormFieldOptionsRepository;
import com.employee.repository.FormMasterRepository;
import com.employee.repository.FormSectionMasterRepository;
import com.employee.repository.FormVersionsRepository;
import com.employee.request.dto.AddFormSectionRequestDto;
import com.employee.request.dto.CreateFormDto;
import com.employee.request.dto.UpdateFormFieldDto;
import com.employee.request.dto.UpdateFormRequestDto;
import com.employee.request.dto.UpdateFormSectionRequestDto;
import com.employee.response.dto.ApiResponseDto;
import com.employee.response.dto.FieldDefinitionCategoryResponseDto;
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
	private final FieldDefinitionMasterRepository fieldDefinitionMasterRepository;
	

	private final FormMasterMapper formMasterMapper;
	private final FormSectionMapper formSectionMapper;
	private final FieldDefinitionMasterMapper fieldDefinitionMasterMapper;
	private final FormFieldMapper formFieldMapper;
	
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

	@Override
	public ResponseEntity<ApiResponseDto> getFormDefination() {

	    List<FieldDefinitionMaster> fieldDefinitions =
	    		fieldDefinitionMasterRepository.findAll();

	    Map<FieldDefinitionCategory, List<FieldDefinitionMaster>> grouped =
	            fieldDefinitions.stream()
	                    .collect(Collectors.groupingBy(
	                            FieldDefinitionMaster::getFieldDefinitionCategory
	                    ));

	    List<FieldDefinitionCategoryResponseDto> response =
	            grouped.entrySet()
	                    .stream()
	                    .map(entry -> FieldDefinitionCategoryResponseDto.builder()
	                            .category(entry.getKey())
	                            .fields(
	                                    fieldDefinitionMasterMapper
	                                            .toResponseDto(entry.getValue())
	                            )
	                            .build()
	                    )
	                    .toList();

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message("Form definitions fetched successfully")
	                    .data(response)
	                    .build()
	    );
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> createFormFields(
	        CreateFormDto createFormDto,
	        HttpServletRequest request) {

	    // Get Form Master
	    FormMaster formMaster = formMasterRepository
	            .findById(createFormDto.getFormMasterId())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Form master not found with id: "
	                                    + createFormDto.getFormMasterId()));

	    // Get Form Section
	    FormSectionMaster formSection = formSectionMasterRepository
	            .findById(createFormDto.getFormSectionId())
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Form section not found with id: "
	                                    + createFormDto.getFormSectionId()));
	    
	    if (createFormDto.getFields() == null
	            || createFormDto.getFields().isEmpty()) {

	        throw new BadRequestException(
	                "At least one form field is required");
	    }

	    // Create fields
	    List<FormFieldMaster> fields = createFormDto.getFields()
	            .stream()
	            .map(fieldDto -> {

	                FormFieldMaster field =
	                        formFieldMapper.toEntity(fieldDto);

	                // Set section
	                field.setFormSectionMaster(formSection);

	                // Set field definition
	                if (fieldDto.getFieldDefinitionId() != null) {

	                    FieldDefinitionMaster fieldDefinition =
	                            fieldDefinitionMasterRepository 
	                                    .findById(
	                                            fieldDto.getFieldDefinitionId())
	                                    .orElseThrow(() ->
	                                            new ResourceNotFoundException(
	                                                    "Field definition not found with id: "
	                                                            + fieldDto.getFieldDefinitionId()));

	                    field.setFieldDefinitionMaster(fieldDefinition);
	                }

	                return field;
	            })
	            .toList();

	    // Save all fields
	    List<FormFieldMaster> savedFields =
	            formFieldMasterRepository.saveAll(fields);

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message(formMaster.getFormName()+" created successfully")
	                   // .data(savedFields)
	                    .build()
	    );
	}

	// ─────────────────────────────────────────────────────────────────────────
	// UPDATE FORM  (update FormFieldMaster records + new FormVersion)
	// ─────────────────────────────────────────────────────────────────────────

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> updateFormFields(
	        UpdateFormRequestDto requestDto,
	        HttpServletRequest request) {

	    // 1. Validate FormMaster exists
	    FormMaster formMaster = formMasterRepository
	            .findById(requestDto.getFormMasterId())
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Form master not found with id: " + requestDto.getFormMasterId()));

	    // 2. Validate FormSectionMaster exists
	    //    Chain: FormSectionMaster → FormVersions → FormMaster
	    FormSectionMaster formSection = formSectionMasterRepository
	            .findById(requestDto.getFormSectionId())
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Form section not found with id: " + requestDto.getFormSectionId()));

	    if (!formSection.getFormVersions().getFormMaster().getFormId()
	            .equals(requestDto.getFormMasterId())) {
	        throw new BadRequestException(
	                "Form section " + requestDto.getFormSectionId()
	                + " does not belong to form " + requestDto.getFormMasterId());
	    }

	    // 3. Batch-fetch all requested field IDs in a single query (avoids N+1)
	    List<UUID> fieldIds = requestDto.getFields().stream()
	            .map(UpdateFormFieldDto::getFieldId)
	            .toList();

	    Map<UUID, FormFieldMaster> existingFieldsMap = formFieldMasterRepository
	            .findAllById(fieldIds)
	            .stream()
	            .collect(Collectors.toMap(FormFieldMaster::getFieldId, f -> f));

	    // Fail fast if any requested field ID was not found
	    List<UUID> missingFieldIds = fieldIds.stream()
	            .filter(id -> !existingFieldsMap.containsKey(id))
	            .toList();
	    if (!missingFieldIds.isEmpty()) {
	        throw new ResourceNotFoundException(
	                "Form fields not found with ids: " + missingFieldIds);
	    }

	    // 4. Batch-fetch all referenced FieldDefinition IDs in a single query (avoids N+1)
	    List<UUID> definitionIds = requestDto.getFields().stream()
	            .map(UpdateFormFieldDto::getFieldDefinitionId)
	            .filter(Objects::nonNull)
	            .distinct()
	            .toList();

	    Map<UUID, FieldDefinitionMaster> definitionsMap = definitionIds.isEmpty()
	            ? Collections.emptyMap()
	            : fieldDefinitionMasterRepository
	                    .findAllById(definitionIds)
	                    .stream()
	                    .collect(Collectors.toMap(
	                            FieldDefinitionMaster::getFieldDefinationId, d -> d));

	    // Fail fast if any referenced definition ID was not found
	    List<UUID> missingDefinitionIds = definitionIds.stream()
	            .filter(id -> !definitionsMap.containsKey(id))
	            .toList();
	    if (!missingDefinitionIds.isEmpty()) {
	        throw new ResourceNotFoundException(
	                "Field definitions not found with ids: " + missingDefinitionIds);
	    }

	    // 5. Apply updates — all lookups from in-memory maps, zero extra queries
	    List<FormFieldMaster> toSave = requestDto.getFields().stream()
	            .map(fieldDto -> {
	                FormFieldMaster existing = existingFieldsMap.get(fieldDto.getFieldId());

	                // Verify field belongs to the requested section
	                if (!existing.getFormSectionMaster().getFormSectionId()
	                        .equals(requestDto.getFormSectionId())) {
	                    throw new BadRequestException(
	                            "Field " + fieldDto.getFieldId()
	                            + " does not belong to section " + requestDto.getFormSectionId());
	                }

	                // Partial update — only overwrite non-null values
	                if (fieldDto.getFieldDescription() != null) existing.setFieldDescription(fieldDto.getFieldDescription());
	                if (fieldDto.getDataType()         != null) existing.setDataType(fieldDto.getDataType());
	                if (fieldDto.getFieldKey()         != null) existing.setFieldKey(fieldDto.getFieldKey());
	                if (fieldDto.getLabel()            != null) existing.setLabel(fieldDto.getLabel());
	                if (fieldDto.getFieldType()        != null) existing.setFieldType(fieldDto.getFieldType());
	                if (fieldDto.getPlaceholder()      != null) existing.setPlaceholder(fieldDto.getPlaceholder());
	                if (fieldDto.getDisabled()         != null) existing.setDisabled(fieldDto.getDisabled());
	                if (fieldDto.getReadonly()         != null) existing.setReadonly(fieldDto.getReadonly());
	                if (fieldDto.getVisible()          != null) existing.setVisible(fieldDto.getVisible());
	                if (fieldDto.getValidationConfig() != null) existing.setValidationConfig(fieldDto.getValidationConfig());
	                if (fieldDto.getSortOrder()        != null) existing.setSortOrder(fieldDto.getSortOrder());
	                if (fieldDto.getIsActive()         != null) existing.setIsActive(fieldDto.getIsActive());
	                if (fieldDto.getIsRequired()       != null) existing.setIsRequired(fieldDto.getIsRequired());
	                if (fieldDto.getIsFilterable()     != null) existing.setIsFilterable(fieldDto.getIsFilterable());
	                if (fieldDto.getAdditionDetail()   != null) existing.setAdditionDetail(fieldDto.getAdditionDetail());

	                // Re-link field definition if provided (already fetched above)
	                if (fieldDto.getFieldDefinitionId() != null) {
	                    existing.setFieldDefinitionMaster(
	                            definitionsMap.get(fieldDto.getFieldDefinitionId()));
	                }

	                return existing;
	            })
	            .toList();

	    formFieldMasterRepository.saveAll(toSave);

	    // 6. Version increment with DB-level lock to prevent race condition
	    int nextVersion = formVersionsRepository
	            .findMaxVersionNumberForUpdate(requestDto.getFormMasterId()) + 1;

	    // publishedAt only stamped when form is actually in PUBLISHED status
	    OffsetDateTime publishedAt = FormStatus.PUBLISHED.equals(formMaster.getFormStatus())
	            ? OffsetDateTime.now()
	            : null;

	    FormVersions newVersion = FormVersions.builder()
	            .formMaster(formMaster)
	            .versionNumber(nextVersion)
	            .formStatus(formMaster.getFormStatus())
	            .publishedAt(publishedAt)
	            .build();

	    formVersionsRepository.save(newVersion);

	    // 7. Return success
	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message(formMaster.getFormName()
	                            + " updated successfully. Version " + nextVersion + " created.")
	                    .build());
	}
	// ─────────────────────────────────────────────────────────────────────────
	// DELETE FORM  (soft-delete FormFieldMaster records + all FormVersions)
	// ─────────────────────────────────────────────────────────────────────────

	@Override
	@Transactional
	public ResponseEntity<ApiResponseDto> deleteFormFields(
	        UUID formId,
	        HttpServletRequest request) {

	    // 1. Validate FormMaster exists
	    FormMaster formMaster = formMasterRepository.findById(formId)
	            .orElseThrow(() -> new ResourceNotFoundException(
	                    "Form not found with id: " + formId));

	    java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
	    String deletedBy = resolveActor(request);

	    // 2. Soft-delete all FormFieldMaster records belonging to this form
	    List<FormFieldMaster> fields =
	            formFieldMasterRepository.findAllByFormSectionMasterFormVersionsFormMasterFormId(formId);

	    fields.forEach(f -> {
	        f.setDeletedDate(now);
	        f.setDeletedBy(deletedBy);
	    });
	    formFieldMasterRepository.saveAll(fields);

	    // 3. Soft-delete all FormVersions belonging to this form
	    List<FormVersions> versions =
	            formVersionsRepository.findAllByFormMasterFormId(formId);

	    versions.forEach(v -> {
	        v.setDeletedDate(now);
	        v.setDeletedBy(deletedBy);
	    });
	    formVersionsRepository.saveAll(versions);

	    return ResponseEntity.ok(
	            ApiResponseDto.builder()
	                    .success(true)
	                    .message("Form '" + formMaster.getFormName() + "' fields and all versions have been deleted.")
	                    .build());
	}

	/**
	 * Resolves the acting user from the request.
	 * Priority: X-User header → X-Username header → "system"
	 */
	private String resolveActor(HttpServletRequest request) {
	    String user = request.getHeader("X-User");
	    if (user == null || user.isBlank()) user = request.getHeader("X-Username");
	    return (user != null && !user.isBlank()) ? user : "system";
	}

}
