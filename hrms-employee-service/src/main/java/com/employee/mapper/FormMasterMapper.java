package com.employee.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.employee.entity.FormMaster;
import com.employee.response.dto.FormResponseDto;

@Mapper(componentModel = "spring")
public interface FormMasterMapper {
	
    FormResponseDto toResponseDto(FormMaster formMaster);

    List<FormResponseDto> toResponseDtoList(List<FormMaster> formMasters);
}