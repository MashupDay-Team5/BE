package com.mashup_team5.domain.treatment.repository;

import com.mashup_team5.domain.treatment.entity.Treatment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

	@Query("SELECT t FROM Treatment t JOIN FETCH t.treatmentCategory "
		+ "ORDER BY t.treatmentCategory.id, t.displayOrder, t.id")
	List<Treatment> findAllWithTreatmentCategory();
}
