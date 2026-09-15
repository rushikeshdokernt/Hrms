package com.auth.mapper;


import org.mapstruct.Mapper;

import com.auth.dto.request.DynamicFormRequest;
import com.auth.dto.response.DynamicFormResponse;
import com.auth.entity.DynamicFormStructure;

@Mapper(componentModel = "spring")
public interface DynamicFormMapper {

    DynamicFormStructure toEntity(DynamicFormRequest request);

	DynamicFormResponse toResponseDto(DynamicFormStructure dynamicFormStructure);
}
