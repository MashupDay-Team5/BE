package com.mashup_team5.domain.review.repository;

import com.mashup_team5.domain.review.entity.ReviewTreatment;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewTreatmentRepository extends JpaRepository<ReviewTreatment, Long> {

	@Query("""
		select rt from ReviewTreatment rt join fetch rt.treatment
		where rt.review.id in :reviewIds
		order by rt.treatment.id asc, rt.id asc
		""")
	List<ReviewTreatment> findAllWithTreatmentByReviewIds(@Param("reviewIds") Collection<Long> reviewIds);
}
