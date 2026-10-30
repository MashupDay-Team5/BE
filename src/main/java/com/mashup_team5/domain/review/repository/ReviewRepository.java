package com.mashup_team5.domain.review.repository;

import com.mashup_team5.domain.review.entity.Review;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	String MATCHING_REVIEWS = """
		where r.hospital.id = :hospitalId
		  and (
		    (:related = true and exists (
		      select rt.id from ReviewTreatment rt
		      where rt.review = r and rt.treatment.id in :treatmentIds
		    ))
		    or (:related = false and not exists (
		      select rt.id from ReviewTreatment rt
		      where rt.review = r and rt.treatment.id in :treatmentIds
		    ))
		  )
		""";

	@Query("select count(r) from Review r " + MATCHING_REVIEWS)
	long countMatchingReviews(
		@Param("hospitalId") Long hospitalId,
		@Param("treatmentIds") Collection<Long> treatmentIds,
		@Param("related") boolean related
	);

	@Query("select r from Review r left join fetch r.user " + MATCHING_REVIEWS + """
		order by
		  case when :sort in ('HIGHEST_RATING', 'LOWEST_RATING') and r.rating is null then 1 else 0 end asc,
		  case when :sort = 'HIGHEST_RATING' then r.rating end desc,
		  case when :sort = 'LOWEST_RATING' then r.rating end asc,
		  case when :sort = 'MOST_HELPFUL' then coalesce(r.helpfulCount, 0) end desc,
		  case when r.createdAt is null then 1 else 0 end asc,
		  r.createdAt desc,
		  r.id desc
		""")
	List<Review> findMatchingReviews(
		@Param("hospitalId") Long hospitalId,
		@Param("treatmentIds") Collection<Long> treatmentIds,
		@Param("related") boolean related,
		@Param("sort") String sort,
		Pageable pageable
	);

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
