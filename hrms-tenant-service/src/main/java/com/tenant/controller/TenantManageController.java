package com.tenant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping(TENANT)
@RequiredArgsConstructor
@Slf4j
public class TenantManageController {

    private final TenantManageService tenantManageService;

    /**
     * POST /api/v1/tenant/add
     * Creates a new tenant with its DB connection details and company profile
     * in a single transactional request.
     */
    @PostMapping(ADD)
    public ResponseEntity<ApiResponseDto> createTenant(
            @Valid @RequestBody AddTenantRequestDto request) {

        log.info("Received add-tenant request for tenantCode [{}]", request.getTenantCode());
        return tenantManageService.createTenant(request);
        
    }
}
