package com.auth.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.auth.dto.response.ModuleListResponse;
import com.auth.entity.ModuleMaster;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

	ModuleListResponse.ModuleResponse toModuleResponse(ModuleMaster moduleMaster);
	
	List<ModuleListResponse.ModuleResponse> toModuleResponseList(
	        List<ModuleMaster> modules
	);
}
