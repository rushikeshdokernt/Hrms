package com.employee.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.employee.entity.FieldDefinitionMaster;
import com.employee.response.dto.FieldDefinitionMasterResponseDto;

@Mapper(componentModel = "spring")
public interface FieldDefinitionMasterMapper {

	FieldDefinitionMasterResponseDto toResponseDto(
            FieldDefinitionMaster entity
    );

    List<FieldDefinitionMasterResponseDto> toResponseDto(
            List<FieldDefinitionMaster> entities
    );
}
