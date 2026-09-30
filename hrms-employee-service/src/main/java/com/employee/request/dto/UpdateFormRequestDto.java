package com.employee.request.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class UpdateFormRequestDto {

    /**
     * The form master this update belongs to (used for FormVersion increment).
     */
    @NotNull(message = "Form master ID is required")
    private UUID formMasterId;

    /**
     * The section whose fields are being updated.
     */
    @NotNull(message = "Form section ID is required")
    private UUID formSectionId;

    /**
     * Fields to update. Each entry must carry the fieldId of the existing FormFieldMaster.
     */
    @Valid
    @NotEmpty(message = "At least one field update is required")
    private List<UpdateFormFieldDto> fields;
}
