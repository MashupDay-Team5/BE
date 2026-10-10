package com.mashup_team5.domain.review.service;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.entity.Review;
import com.mashup_team5.domain.review.entity.ReviewKeyword;
import com.mashup_team5.domain.review.repository.ReviewKeywordRepository;
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
	private final TransactionTemplate transactionTemplate;

	// 자동 갱신 작업에서 저장된 병원·시술을 전달한다. 후기 부족은 false, 성공한 빈 결과는 true다.
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean generateAndSave(HospitalTreatment hospitalTreatment) {
		List<String> reviews = sourceService.findEligibleReviews(
			hospitalTreatment.getHospital().getId(), hospitalTreatment.getTreatment().getId()).stream()
			.map(Review::getContent)
			.toList();
		if (reviews.size() < ReviewKeywordRefreshPolicy.MIN_REVIEW_COUNT) {
			return false;
		}
		List<String> keywords = extractionService.extractKeywords(reviews);
		transactionTemplate.executeWithoutResult(status -> {
			LocalDateTime now = LocalDateTime.now();
			reviewKeywordRepository.deleteAllByHospitalTreatmentId(hospitalTreatment.getId());
			reviewKeywordRepository.saveAllAndFlush(keywords.stream()
				.map(keyword -> ReviewKeyword.builder().hospitalTreatment(hospitalTreatment).keyword(keyword)
					.createdAt(now).updatedAt(now).build())
				.toList());
		});
		return true;
	}
}
