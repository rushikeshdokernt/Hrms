package com.auth.dto.request;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRoleRequestDto {

    private String roleName;

    private String description;

    private Map<UUID, List<String>> usecases;
}
