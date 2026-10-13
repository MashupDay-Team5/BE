package com.mashup_team5.domain.treatment.repository;

import com.mashup_team5.domain.treatment.entity.TreatmentCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TreatmentCategoryRepository extends JpaRepository<TreatmentCategory, Long> {

	@Query("SELECT tc FROM TreatmentCategory tc JOIN FETCH tc.medicalCategory ORDER BY tc.medicalCategory.id, tc.id")
	List<TreatmentCategory> findAllWithMedicalCategory();
}
