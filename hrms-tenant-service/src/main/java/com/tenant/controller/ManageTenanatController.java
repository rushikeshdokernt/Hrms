package com.tenant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.tenant.constant.ApiConstant.TENANT;
import static com.tenant.constant.ApiConstant.ADD;

@RestController
@RequestMapping(TENANT)
public class ManageTenanatController {

	@PostMapping(ADD)
	public ResponseEntity<?> createTenant(){
		return null;
	}
}
