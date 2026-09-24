package com.employee.constant;

public final class ApiConstants {

	public static final String BASE="/api/v1";
	
	public static final String EMPLOYEE=BASE+"/employee";
	
	//========================Onboarding =========================
	
	public static final String FORM = "/form";
	
	//==================== Admin Config ============================
	
	public static final String ADMIN_CONFIG= EMPLOYEE+"/admin-config";
	
	public static final String FORM_FIELD= ADMIN_CONFIG+"/form-fields";
	
	public static final String SECTION= "/section";
	
	public static final String DELETE_SECTION = "/section/{formSectionId}";
	
	
	
}
