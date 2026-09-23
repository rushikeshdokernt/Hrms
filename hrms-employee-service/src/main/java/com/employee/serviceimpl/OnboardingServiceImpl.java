
package com.employee.serviceimpl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.employee.entity.FormFieldMaster;
import com.employee.entity.FormMaster;
import com.employee.entity.FormSectionMaster;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.FormFieldMasterRepository;
import com.employee.repository.FormFieldOptionsRepository;
import com.employee.repository.FormFieldValidationsRepository;
import com.employee.repository.FormMasterRepository;
import com.employee.repository.FormSectionMasterRepository;
import com.employee.response.dto.ApiResponseDto;
import com.employee.response.dto.FormFieldOptionResponseDto;
import com.employee.response.dto.FormFieldResponseDto;
import com.employee.response.dto.FormFieldValidationResponseDto;
import com.employee.response.dto.FormResponseDto;
import com.employee.response.dto.FormSectionResponseDto;
import com.employee.service.OnboardingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final FormMasterRepository formMasterRepository;
    private final FormSectionMasterRepository formSectionMasterRepository;
    private final FormFieldMasterRepository formFieldMasterRepository;
    private final FormFieldOptionsRepository formFieldOptionsRepository;
    private final FormFieldValidationsRepository formFieldValidationsRepository;

    @Override
    public ResponseEntity<ApiResponseDto> fetchForm(String formName) {

        // 1. Fetch form
        FormMaster formMaster = formMasterRepository
                .findByFormName(formName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Form not exists with name " + formName
                        )
                );

        // 2. Fetch sections
        List<FormSectionMaster> formSectionList =
                formSectionMasterRepository
                        .findByFormMasterFormId(formMaster.getFormId());

        // 3. Map sections
        List<FormSectionResponseDto> sectionResponseList =
                formSectionList.stream()
                        .map(this::mapSection)
                        .toList();

        // 4. Build form response
        FormResponseDto response = FormResponseDto.builder()
                .formId(formMaster.getFormId())
                .formName(formMaster.getFormName())
                .visible(formMaster.getVisible())
                .sortOrder(formMaster.getSortOrder())
                .sections(sectionResponseList)
                .build();

        // 5. Return response
        return ResponseEntity.ok(
                ApiResponseDto.builder()
                		.success(true)
                        .message("Form fetched successfully")
                        .data(response)
                        .build()
        );
    }

    private FormSectionResponseDto mapSection(FormSectionMaster section) {

        List<FormFieldMaster> fieldList =
                formFieldMasterRepository
                        .findByFormSectionMasterFormSectionId(
                                section.getFormSectionId()
                        );

        List<FormFieldResponseDto> fieldResponseList =
                fieldList.stream()
                        .map(this::mapField)
                        .toList();

        return FormSectionResponseDto.builder()
                .formSectionId(section.getFormSectionId())
                .sectionName(section.getSectionName())
                .visible(section.getVisible())
                .sortOrder(section.getSortOrder())
                .immutable(section.getImmutable())
                .fields(fieldResponseList)
                .build();
    }

    private FormFieldResponseDto mapField(FormFieldMaster field) {

        
        List<FormFieldOptionResponseDto> optionResponseList = List.of();

        if ("DROPDOWN".equalsIgnoreCase(field.getFieldType())) {

            optionResponseList =
                    formFieldOptionsRepository
                            .findByFormFieldMasterFieldId(
                                    field.getFieldId()
                            )
                            .stream()
                            .map(option ->
                                    FormFieldOptionResponseDto.builder()
                                            .fieldOptionId(
                                                    option.getFieldOptionId()
                                            )
                                            .optionLabel(
                                                    option.getOptionLabel()
                                            )
                                            .optionValue(
                                                    option.getOptionValue()
                                            )
                                            .sortOrder(
                                                    option.getSortOrder()
                                            )
                                            .build()
                            )
                            .toList();
        }

        
        List<FormFieldValidationResponseDto> validationResponseList =
                formFieldValidationsRepository
                        .findByFormFieldMasterFieldId(
                                field.getFieldId()
                        )
                        .stream()
                        .map(validation ->
                                FormFieldValidationResponseDto.builder()
                                        .fieldValidationId(
                                                validation.getFieldValidationId()
                                        )
                                        .validationType(
                                                validation.getValidationType()
                                        )
                                        .validationValue(
                                                validation.getValidationValue()
                                        )
                                        .errorMessage(
                                                validation.getErrorMessage()
                                        )
                                        .sortOrder(
                                                validation.getSortOrder()
                                        )
                                        .build()
                        )
                        .toList();

        return FormFieldResponseDto.builder()
                .fieldId(field.getFieldId())
                .fieldKey(field.getFieldKey())
                .label(field.getLabel())
                .fieldType(field.getFieldType())
                .placeholder(field.getPlaceholder())
                .defaultValue(field.getDefaultValue())
                .required(field.getRequired())
                .disabled(field.getDisabled())
                .readonly(field.getReadonly())
                .visible(field.getVisible())
                .validation(field.getValidation())
                .sortOrder(field.getSortOrder())
                .immutable(field.getImmutable())
                .options(optionResponseList)
                .validations(validationResponseList)
                .build();
    }
}

