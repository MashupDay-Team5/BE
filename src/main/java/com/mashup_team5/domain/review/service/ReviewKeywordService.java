package com.mashup_team5.domain.review.service;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.entity.Review;
import com.mashup_team5.domain.review.entity.ReviewKeyword;
import com.mashup_team5.domain.review.repository.ReviewKeywordRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class ReviewKeywordService {

	private final ReviewKeywordSourceService sourceService;
	private final ReviewKeywordExtractionService extractionService;
	private final ReviewKeywordRepository reviewKeywordRepository;
	private final EntityManager entityManager;
	private final ReviewKeywordRefreshPolicy refreshPolicy;
	private final TransactionTemplate transactionTemplate;

	// 자동 갱신 작업에서 호출한다. 조건 미충족은 false, 성공한 빈 결과는 true다.
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean refreshIfNeeded(HospitalTreatment hospitalTreatment) {
		HospitalTreatment target = entityManager.find(HospitalTreatment.class, hospitalTreatment.getId());
		if (target == null) {
			return false;
		}
		List<Review> reviews = sourceService.findEligibleReviews(
			target.getHospital().getId(), target.getTreatment().getId());
		if (!shouldRefresh(target.getId(), reviews)) {
			return false;
		}
		List<String> keywords = extractionService.extractKeywords(reviews.stream().map(Review::getContent).toList());
		saveResult(target, reviews, keywords);
		return true;
	}

	private boolean shouldRefresh(Long hospitalTreatmentId, List<Review> reviews) {
		ReviewKeyword previousResult = reviewKeywordRepository.findByHospitalTreatmentId(hospitalTreatmentId).orElse(null);
		if (previousResult == null) {
			return refreshPolicy.shouldRefresh(reviews.size(), 0, null, LocalDateTime.now());
		}
		long addedReviewCount = reviews.stream()
			.filter(review -> review.getId() > previousResult.getLastAnalyzedReviewId())
			.count();
		return refreshPolicy.shouldRefresh(reviews.size(), addedReviewCount, previousResult.getUpdatedAt(),
			LocalDateTime.now());
	}

	private void saveResult(HospitalTreatment target, List<Review> reviews, List<String> keywords) {
		Long lastAnalyzedReviewId = reviews.stream().map(Review::getId).max(Long::compareTo).orElseThrow();
		transactionTemplate.executeWithoutResult(status -> {
			LocalDateTime now = LocalDateTime.now();
			ReviewKeyword result = reviewKeywordRepository.findByHospitalTreatmentId(target.getId())
				.orElseGet(() -> ReviewKeyword.builder().hospitalTreatment(target).createdAt(now).build());
			result.updateResult(keywords, lastAnalyzedReviewId, now);
			reviewKeywordRepository.saveAndFlush(result);
		});
	}
}
