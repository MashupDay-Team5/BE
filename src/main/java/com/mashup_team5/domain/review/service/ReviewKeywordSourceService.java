package com.mashup_team5.domain.review.service;

import com.mashup_team5.domain.review.entity.Review;
import com.mashup_team5.domain.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewKeywordSourceService {

	private final ReviewRepository reviewRepository;

	public List<Review> findEligibleReviews(Long hospitalId, Long treatmentId) {
		return reviewRepository.findKeywordSourceReviews(hospitalId, treatmentId).stream()
			.filter(review -> !review.getContent().isBlank())
			.toList();
	}
}
