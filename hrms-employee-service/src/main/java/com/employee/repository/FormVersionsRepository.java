package com.employee.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.employee.entity.FormVersions;

public interface FormVersionsRepository extends JpaRepository<FormVersions, UUID> {

	Optional<FormVersions> findByFormMasterFormId(UUID formId);

	Optional<FormVersions> findByFormMasterFormName(String formName);

	/**
	 * Returns the highest version number for a given form.
	 * Used to calculate the next version number on update.
	 */

	@Query(value = """
	        SELECT COALESCE(MAX(fv.version_number), 0)
	        FROM   form_versions fv
	        WHERE  fv.form_id = (
	            SELECT fv2.form_id
	            FROM   form_versions fv2
	            WHERE  fv2.form_id = :formMasterId
	            LIMIT  1
	            FOR UPDATE
	        )
	        """,
	       nativeQuery = true)
	int findMaxVersionNumberForUpdate(@Param("formMasterId") UUID formMasterId);
	/**
	 * Returns all non-deleted versions for a given form.
	 * Used for bulk soft-delete on form deletion.
	 */
	List<FormVersions> findAllByFormMasterFormId(UUID formId);
}
