package com.auth.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auth.dto.response.ModuleUsecasesListResponse;
import com.auth.entity.ModuleMaster;
import com.auth.entity.UseCase;

@Mapper(componentModel = "spring")
public interface ModuleUsecasesMapper {

    @Mapping(target = "useCases", ignore = true)
    ModuleUsecasesListResponse.ModuleResponse toModuleResponse(
            ModuleMaster module
    );

    List<ModuleUsecasesListResponse.ModuleResponse> toModuleResponseList(
            List<ModuleMaster> modules
    );

    ModuleUsecasesListResponse.UseCaseResponse toUseCaseResponse(
            UseCase useCase
    );

    List<ModuleUsecasesListResponse.UseCaseResponse> toUseCaseResponseList(
            List<UseCase> useCases
    );
}
