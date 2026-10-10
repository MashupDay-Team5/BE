package com.mashup_team5.domain.review.repository;

import com.mashup_team5.domain.review.entity.ReviewKeyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {

	List<ReviewKeyword> findAllByHospitalTreatmentIdOrderByIdAsc(Long hospitalTreatmentId);

	@Modifying
	@Query("delete from ReviewKeyword k where k.hospitalTreatment.id = :hospitalTreatmentId")
	void deleteAllByHospitalTreatmentId(@Param("hospitalTreatmentId") Long hospitalTreatmentId);
}
