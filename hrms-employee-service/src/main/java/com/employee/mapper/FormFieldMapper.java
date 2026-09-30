package com.employee.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.employee.entity.FormFieldMaster;
import com.employee.request.dto.CreateFormFieldDto;

@Mapper(componentModel = "spring")
public interface FormFieldMapper {

    @Mapping(target = "fieldId", ignore = true)
    @Mapping(target = "formSectionMaster", ignore = true)
    @Mapping(target = "fieldDefinitionMaster", ignore = true)
    FormFieldMaster toEntity(CreateFormFieldDto dto);
}
