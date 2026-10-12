package com.mashup_team5.domain.review.repository;

import com.mashup_team5.domain.review.entity.ReviewKeyword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewKeywordRepository extends JpaRepository<ReviewKeyword, Long> {

	Optional<ReviewKeyword> findByHospitalTreatmentId(Long hospitalTreatmentId);
}
