package com.tenant.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenant.dto.request.AddTenantRequestDto;
import com.tenant.dto.response.AddTenantResponseDto;
import com.tenant.dto.response.ApiResponseDto;
import com.tenant.entity.TenantProfile;
import com.tenant.entity.TenantsDBDetails;
import com.tenant.enums.TenantStatus;
import com.tenant.exception.DuplicateResourceException;
import com.tenant.mapper.TenantProfileMapper;
import com.tenant.mapper.TenantsDBDetailsMapper;
import com.tenant.repository.TenantDBDetailsRepository;
import com.tenant.repository.TenantProfileRepository;
import com.tenant.service.TenantManageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantManageServiceImpl implements TenantManageService {

    private final TenantDBDetailsRepository tenantDBDetailsRepository;
    private final TenantProfileRepository   tenantProfileRepository;
    private final TenantsDBDetailsMapper    tenantsDBDetailsMapper;
    private final TenantProfileMapper       tenantProfileMapper;

    @Override
    @Transactional
    public ResponseEntity<ApiResponseDto> createTenant(AddTenantRequestDto request) {

        // ── 1. Uniqueness guards ─────────────────────────────────────────────
        if (tenantDBDetailsRepository.findByTenantCode(request.getTenantCode()).isPresent()) {
            throw new DuplicateResourceException(
                    "Tenant with code '" + request.getTenantCode() + "' already exists");
        }
        if (tenantDBDetailsRepository.findBySubdomain(request.getSubdomain()).isPresent()) {
            throw new DuplicateResourceException(
                    "Tenant with subdomain '" + request.getSubdomain() + "' already exists");
        }
        if (tenantDBDetailsRepository.findByDomainUrl(request.getDomainUrl()).isPresent()) {
            throw new DuplicateResourceException(
                    "Tenant with domain URL '" + request.getDomainUrl() + "' already exists");
        }

        // ── 2. Map request → TenantsDBDetails entity and save ────────────────
        TenantsDBDetails dbDetails = tenantsDBDetailsMapper.toEntity(request);
//        dbDetails.setStatus(TenantStatus.INACTIVE);
        TenantsDBDetails savedDbDetails = tenantDBDetailsRepository.save(dbDetails);
        log.info("Saved TenantsDBDetails for tenant [{}] with id [{}]",
                savedDbDetails.getTenantCode(), savedDbDetails.getTenantId());

        // ── 3. Map request → TenantProfile entity, wire FK, and save ─────────
        TenantProfile profile = tenantProfileMapper.toEntity(request);

        // The mapper ignores tenantsDBDetails; we set it via the relation field
        // using the saved entity reference so JPA has the correct FK.
        // NOTE: We use the generated setter from Lombok @Setter here only to
        // satisfy the JPA FK requirement; all other field mapping is via mapper.
        profile.setTenantsDBDetails(savedDbDetails);

        TenantProfile savedProfile = tenantProfileRepository.save(profile);
        log.info("Saved TenantProfile [{}] for tenant [{}]",
                savedProfile.getTenantProfileId(), savedDbDetails.getTenantCode());

        // ── 4. Assemble response ─────────────────────────────────────────────
//        AddTenantResponseDto responseDto = tenantProfileMapper.toResponseDto(savedDbDetails, savedProfile);
        
        ApiResponseDto responseDto = ApiResponseDto.builder()
				.success(true)
				.message("Tenant added successfully")
				.build();

         return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
