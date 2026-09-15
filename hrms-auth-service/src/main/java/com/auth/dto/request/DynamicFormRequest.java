package com.auth.dto.request;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DynamicFormRequest {

	private String formType;
	
	private JsonNode formFields;
}
