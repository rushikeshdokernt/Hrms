package com.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.auth.dto.request.SuperAdminRegisterRequest;
import com.auth.entity.UserAccounts;

@Mapper(componentModel = "spring")
public interface UserAccountsMapper {

	@Mapping(target = "userAccountId", ignore = true)
    @Mapping(target = "employeeId", source = "employeeId")
    @Mapping(target = "additionDetail", ignore = true)
    UserAccounts toEntity(SuperAdminRegisterRequest request);

    
}
