package com.auth.util;

import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.auth.entity.DynamicFormStructure;
import com.auth.exception.InvalidRequestException;
import com.auth.repository.DynamicFormRepository;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DynamicValidationUtil {

    private final DynamicFormRepository dynamicFormRepository;

    public void validate(String formType,JsonNode loginRequest) {

        DynamicFormStructure formStructure =
                dynamicFormRepository.findByFormType(formType);

        if (formStructure == null ||
                formStructure.getFormFields() == null) {

            throw new InvalidRequestException(
                    "LOGIN form configuration not found"
            );
        }

        JsonNode formFields =
                formStructure.getFormFields();

        /*
         * formFields:
         *
         * [
         *   {
         *     "fields": [...]
         *   }
         * ]
         */

        if (!formFields.isArray() || formFields.isEmpty()) {
            throw new InvalidRequestException(
                    "Invalid LOGIN form configuration"
            );
        }

        JsonNode fields =
                formFields.get(0).path("fields");

        if (!fields.isArray()) {
            throw new InvalidRequestException(
                    "LOGIN fields are not configured correctly"
            );
        }

        for (JsonNode field : fields) {

            String fieldId =
                    field.path("fieldId").asText(null);

            if (fieldId == null || fieldId.isBlank()) {
                continue;
            }

            /*
             * EMAIL -> email
             * CONTACT -> contact
             * PASSWORD -> password
             */
            String requestField =
                    fieldId.toLowerCase(Locale.ROOT);

            JsonNode value =
                    loginRequest.get(requestField);

            JsonNode validations =
                    field.path("validations");

            if (!validations.isArray()) {
                continue;
            }

            for (JsonNode validation : validations) {

                applyValidation(
                        requestField,
                        value,
                        validation
                );
            }
        }
    }

    private void applyValidation(
            String fieldName,
            JsonNode value,
            JsonNode validation) {

        String validationType =
                validation.path("type").asText(null);

        if (validationType == null) {
            return;
        }

        String message =
                validation.path("message")
                        .asText("Invalid " + fieldName);

        switch (validationType.toUpperCase(Locale.ROOT)) {

            case "REQUIRED":

                validateRequired(
                        value,
                        validation,
                        message
                );

                break;

            case "MIN_LENGTH":

                validateMinLength(
                        value,
                        validation,
                        message
                );

                break;

            case "MAX_LENGTH":

                validateMaxLength(
                        value,
                        validation,
                        message
                );

                break;

            case "REGEX":

                validateRegex(
                        value,
                        validation,
                        message
                );

                break;

            default:

                throw new InvalidRequestException(
                        "Unsupported validation type: "
                                + validationType
                                + " for field "
                                + fieldName
                );
        }
    }

    private void validateRequired(
            JsonNode value,
            JsonNode validation,
            String message) {

        boolean required =
                validation.path("value").asBoolean(false);

        if (!required) {
            return;
        }

        if (value == null ||
                value.isNull() ||
                value.asText().isBlank()) {

            throw new InvalidRequestException(message);
        }
    }

    private void validateMinLength(
            JsonNode value,
            JsonNode validation,
            String message) {

        if (isEmpty(value)) {
            return;
        }

        int minLength =
                validation.path("value").asInt();

        if (value.asText().length() < minLength) {
            throw new InvalidRequestException(message);
        }
    }

    private void validateMaxLength(
            JsonNode value,
            JsonNode validation,
            String message) {

        if (isEmpty(value)) {
            return;
        }

        int maxLength =
                validation.path("value").asInt();

        if (value.asText().length() > maxLength) {
            throw new InvalidRequestException(message);
        }
    }

    private void validateRegex(
            JsonNode value,
            JsonNode validation,
            String message) {

        if (isEmpty(value)) {
            return;
        }

        String regex =
                validation.path("value").asText();

        if (!value.asText().matches(regex)) {
            throw new InvalidRequestException(message);
        }
    }

    private boolean isEmpty(JsonNode value) {

        return value == null ||
                value.isNull() ||
                value.asText().isBlank();
    }
}
