package com.tenant.serviceimpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenant.dto.request.AddTenantRequestDto;
import com.tenant.dto.response.ApiResponseDto;
import com.tenant.dto.response.TenantListItemDto;
import com.tenant.entity.TenantProfile;
import com.tenant.entity.TenantsDBDetails;
import com.tenant.exception.DuplicateResourceException;
import com.tenant.mapper.TenantListMapper;
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
    private final TenantListMapper          tenantListMapper;

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
        TenantsDBDetails savedDbDetails = tenantDBDetailsRepository.save(dbDetails);
        log.info("Saved TenantsDBDetails for tenant [{}] with id [{}]",
                savedDbDetails.getTenantCode(), savedDbDetails.getTenantId());

        // ── 3. Map request → TenantProfile entity, wire FK, and save ─────────
        TenantProfile profile = tenantProfileMapper.toEntity(request);
        profile.setTenantsDBDetails(savedDbDetails);

        TenantProfile savedProfile = tenantProfileRepository.save(profile);
        log.info("Saved TenantProfile [{}] for tenant [{}]",
                savedProfile.getTenantProfileId(), savedDbDetails.getTenantCode());

        // ── 4. Assemble response ─────────────────────────────────────────────
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDto.builder()
                        .success(true)
                        .message("Tenant added successfully")
                        .build());
    }

    @Override
    public ResponseEntity<ApiResponseDto> getAllTenants(Pageable pageable) {

        // Single JOIN FETCH query with pagination — no N+1
        Page<TenantProfile> page = tenantProfileRepository.findAllWithDbDetails(pageable);
        List<TenantListItemDto> content = tenantListMapper.toDtoList(page.getContent());

        log.info("Fetched page [{}/{}] with [{}] tenants",
                page.getNumber() + 1, page.getTotalPages(), content.size());

        // Build a lightweight pagination metadata map alongside the content
        Map<String, Object> pageData = new LinkedHashMap<>();
        pageData.put("content",       content);
        pageData.put("page",          page.getNumber());
        pageData.put("size",          page.getSize());
        pageData.put("totalElements", page.getTotalElements());
        pageData.put("totalPages",    page.getTotalPages());
        pageData.put("last",          page.isLast());

        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .success(true)
                        .message("Tenants fetched successfully")
                        .data(pageData)
                        .build());
    }
}
