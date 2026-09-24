package com.employee.request.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class AddFormSectionRequestDto {

    @NotNull(message = "Form ID is required")
    private UUID formId;

    @NotBlank(message = "Section name is required")
    @Size(max = 1000, message = "Section name must not exceed 1000 characters")
    private String sectionName;

    @NotNull(message = "Sort order is required")
    private Integer sortOrder;

}
