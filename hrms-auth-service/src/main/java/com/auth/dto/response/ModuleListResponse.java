package com.auth.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModuleListResponse {

	 private List<ModuleResponse> modules;

	    @Setter
	    @Getter
	    public static class ModuleResponse {

	        private UUID moduleId;
	        private String moduleName;
	       // private List<UseCaseResponse> useCases;
	    }

	    @Setter
	    @Getter
	    public static class UseCaseResponse {

	        private UUID useCaseId;
	        private String useCaseName;
	    }
}
