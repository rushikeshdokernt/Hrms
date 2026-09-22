package com.tenant.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tenant.dto.request.AddTenantRequestDto;
import com.tenant.dto.response.ApiResponseDto;
import com.tenant.service.TenantManageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.tenant.constant.ApiConstant.TENANT;
import static com.tenant.constant.ApiConstant.ADD;
import static com.tenant.constant.ApiConstant.VIEW;

@RestController
@RequestMapping(TENANT)
@RequiredArgsConstructor
@Slf4j
public class TenantManageController {

    private final TenantManageService tenantManageService;

    /**
     * POST /api/v1/tenant/add
     * Creates a new tenant with its DB connection details and company profile.
     */
    @PostMapping(ADD)
    public ResponseEntity<ApiResponseDto> createTenant(
            @Valid @RequestBody AddTenantRequestDto request) {

        log.info("Received add-tenant request for tenantCode [{}]", request.getTenantCode());
        return tenantManageService.createTenant(request);
    }

    /**
     * GET /api/v1/tenant/view
     * Returns a paginated list of all tenants.
     *
     * Query params (all optional):
     *   page  — 0-based page number (default 0)
     *   size  — items per page      (default 10)
     *   sort  — field,direction     (e.g. tenantName,asc)
     */
    @GetMapping(VIEW)
    public ResponseEntity<ApiResponseDto> getAllTenants(
            @PageableDefault(size = 10) Pageable pageable) {

        log.info("Received get-all-tenants request — page [{}], size [{}]",
                pageable.getPageNumber(), pageable.getPageSize());
        return tenantManageService.getAllTenants(pageable);
    }
}
