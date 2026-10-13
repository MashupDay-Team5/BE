package com.mashup_team5.domain.review.repository;

import com.mashup_team5.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Query("""
		select r from Review r
		where r.hospital.id = :hospitalId
		  and r.isReceiptVerified = true
		  and r.content is not null
		  and trim(r.content) <> ''
		  and exists (
		      select rt.id from ReviewTreatment rt
		      where rt.review = r and rt.treatment.id = :treatmentId
		  )
		order by r.createdAt desc, r.id desc
		""")
	List<Review> findKeywordSourceReviews(@Param("hospitalId") Long hospitalId,
		@Param("treatmentId") Long treatmentId);
}
