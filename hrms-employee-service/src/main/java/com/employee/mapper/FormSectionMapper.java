package com.employee.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.employee.entity.FormSectionMaster;
import com.employee.request.dto.AddFormSectionRequestDto;
import com.employee.response.dto.FormSectionResponseDto;

@Mapper(componentModel = "spring")
public interface FormSectionMapper {

    @Mapping(target = "formSectionId", ignore = true)
    @Mapping(target = "formMaster", ignore = true)
    @Mapping(target = "visible", ignore = true)
    @Mapping(target = "additionDetail", ignore = true)
    @Mapping(target = "immutable", ignore = true)
    FormSectionMaster toEntity(AddFormSectionRequestDto request);

    @Mapping(target = "fields", ignore = true)
    FormSectionResponseDto toResponse(FormSectionMaster entity);
}
