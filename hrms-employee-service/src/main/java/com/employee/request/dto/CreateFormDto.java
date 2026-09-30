package com.employee.request.dto;

import java.util.List;
import java.util.UUID;

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
public class CreateFormDto {

    private UUID formMasterId;

    private UUID formSectionId;

    private List<CreateFormFieldDto> fields;
}