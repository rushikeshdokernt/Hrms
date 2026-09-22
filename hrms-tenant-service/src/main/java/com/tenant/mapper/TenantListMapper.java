package com.tenant.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tenant.dto.response.TenantListItemDto;
import com.tenant.entity.TenantProfile;

@Mapper(componentModel = "spring")
public interface TenantListMapper {

    /**
     * Maps a single TenantProfile (with its nested TenantsDBDetails FK)
     * to a flat TenantListItemDto for the get-all-tenants response.
     *
     * MapStruct traverses the @OneToOne relation automatically using
     * dot-notation on the source (tenantsDBDetails.*).
     * dbPasswordSecret is intentionally NOT mapped.
     */
    @Mapping(target = "tenantId",       source = "tenantsDBDetails.tenantId")
    @Mapping(target = "tenantCode",     source = "tenantsDBDetails.tenantCode")
    @Mapping(target = "subdomain",      source = "tenantsDBDetails.subdomain")
    @Mapping(target = "domainUrl",      source = "tenantsDBDetails.domainUrl")
    @Mapping(target = "dbHost",         source = "tenantsDBDetails.dbHost")
    @Mapping(target = "dbPort",         source = "tenantsDBDetails.dbPort")
    @Mapping(target = "dbName",         source = "tenantsDBDetails.dbName")
    @Mapping(target = "dbUser",         source = "tenantsDBDetails.dbUser")
    @Mapping(target = "status",         source = "tenantsDBDetails.status")
    @Mapping(target = "type",           source = "tenantsDBDetails.type")
    @Mapping(target = "timezone",       source = "tenantsDBDetails.timezone")
    @Mapping(target = "locale",         source = "tenantsDBDetails.locale")
    @Mapping(target = "createdDate",    source = "tenantsDBDetails.createdDate")
    @Mapping(target = "updatedDate",    source = "tenantsDBDetails.updatedDate")
    @Mapping(target = "tenantProfileId", source = "tenantProfileId")
    @Mapping(target = "tenantName",     source = "tenantName")
    @Mapping(target = "tenantSize",     source = "tenantSize")
    @Mapping(target = "gstin",          source = "gstin")
    @Mapping(target = "pan",            source = "pan")
    @Mapping(target = "address",        source = "address")
    @Mapping(target = "pincode",        source = "pincode")
    @Mapping(target = "countryCode",    source = "countryCode")
    @Mapping(target = "currency",       source = "currency")
    @Mapping(target = "contactName",    source = "contactName")
    @Mapping(target = "contactEmail",   source = "contactEmail")
    @Mapping(target = "contactPhone",   source = "contactPhone")
    TenantListItemDto toDto(TenantProfile profile);

    /**
     * Maps a list of TenantProfile entities to a list of TenantListItemDto.
     * MapStruct auto-generates this by delegating each element to toDto().
     */
    List<TenantListItemDto> toDtoList(List<TenantProfile> profiles);
}
