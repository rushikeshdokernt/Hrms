package com.auth.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionDto {

    private boolean viewAccess;
    private boolean createAccess;
    private boolean editAccess;
    private boolean deleteAccess;
}
