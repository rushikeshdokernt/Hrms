package com.tenant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tenant.dto.request.AddTenantRequestDto;
import com.tenant.entity.TenantProfile;

@Mapper(componentModel = "spring")
public interface TenantProfileMapper {

    /**
     * Maps AddTenantRequestDto → TenantProfile entity.
     * tenantsDBDetails (the FK relation) is set explicitly in the service
     * after both entities are saved, so it is ignored here.
     */
    @Mapping(target = "tenantProfileId",  ignore = true)
    @Mapping(target = "tenantsDBDetails", ignore = true)
    TenantProfile toEntity(AddTenantRequestDto request);
}

