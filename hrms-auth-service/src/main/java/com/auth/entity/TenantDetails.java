package com.auth.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantDetails extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID tenantDetailId;
	
	private UUID tenantId;
	
	private String tenantName;
}
