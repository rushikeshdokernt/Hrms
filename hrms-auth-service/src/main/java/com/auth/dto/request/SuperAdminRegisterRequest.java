package com.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SuperAdminRegisterRequest {

	private String employeeId;

    private String username;
    
    private String password;
    
    private String email;
    
    private String accountStatus;
    
    private String roleName;
    
    //private String tenantId;
}
