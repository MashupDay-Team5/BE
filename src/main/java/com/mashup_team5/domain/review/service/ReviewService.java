package com.mashup_team5.domain.review.service;

import com.mashup_team5.domain.hospital.repository.HospitalTreatmentRepository;
import com.mashup_team5.domain.review.dto.request.ReviewListReq;
import com.mashup_team5.domain.review.dto.response.ReviewListRes;
import com.mashup_team5.domain.review.dto.response.ReviewListRes.ReceivedTreatmentRes;
import com.mashup_team5.domain.review.dto.response.ReviewListRes.ReviewItemRes;
import com.mashup_team5.domain.review.entity.Review;
import com.mashup_team5.domain.review.entity.ReviewTreatment;
import com.mashup_team5.domain.review.enums.ReviewScope;
import com.mashup_team5.domain.review.enums.ReviewSort;
import com.mashup_team5.domain.review.repository.ReviewRepository;
import com.mashup_team5.domain.review.repository.ReviewTreatmentRepository;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final HospitalTreatmentRepository hospitalTreatmentRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewTreatmentRepository reviewTreatmentRepository;

	public ReviewListRes getReviews(Long hospitalId, Long treatmentId, ReviewListReq request) {
		validateIds(hospitalId, treatmentId);
		validatePagination(request.getPage(), request.getSize());
		ReviewScope scope = ReviewScope.from(request.getScope());
		ReviewSort sort = ReviewSort.from(request.getSort());
		if (!hospitalTreatmentRepository.existsByHospital_IdAndTreatment_Id(hospitalId, treatmentId)) {
			throw new CustomException(ErrorCode.HOSPITAL_TREATMENT_NOT_FOUND);
		}

		PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
		boolean related = scope == ReviewScope.RELATED;
		long totalCount = reviewRepository.countMatchingReviews(hospitalId, treatmentId, related);
		if (pageable.getOffset() >= totalCount) {
			return new ReviewListRes(
				hospitalId, treatmentId, scope, List.of(), totalCount, request.getPage(), request.getSize(), false);
		}

		List<Review> reviews = reviewRepository.findMatchingReviews(
			hospitalId, treatmentId, related, sort.name(), pageable);
		Map<Long, List<ReceivedTreatmentRes>> treatments = getReceivedTreatments(reviews);
		List<ReviewItemRes> items = reviews.stream()
			.map(review -> ReviewItemRes.from(review, treatments.getOrDefault(review.getId(), List.of())))
			.toList();
		boolean hasNext = !items.isEmpty() && pageable.getOffset() + items.size() < totalCount;
		return new ReviewListRes(
			hospitalId, treatmentId, scope, items, totalCount, request.getPage(), request.getSize(), hasNext);
	}

	private void validateIds(Long hospitalId, Long treatmentId) {
		if (hospitalId == null || treatmentId == null || hospitalId < 1 || treatmentId < 1) {
			throw new CustomException(ErrorCode.INVALID_ID);
		}
	}

	private void validatePagination(Integer page, Integer size) {
		if (page == null || size == null || page < 0 || size < 1 || size > 50) {
			throw new CustomException(ErrorCode.INVALID_PAGINATION);
		}
	}

	private Map<Long, List<ReceivedTreatmentRes>> getReceivedTreatments(List<Review> reviews) {
		if (reviews.isEmpty()) {
			return Map.of();
		}
		List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
		Map<Long, Map<Long, ReceivedTreatmentRes>> uniqueTreatments = new HashMap<>();
		for (ReviewTreatment link : reviewTreatmentRepository.findAllWithTreatmentByReviewIds(reviewIds)) {
			ReceivedTreatmentRes treatment = ReceivedTreatmentRes.from(link.getTreatment());
			uniqueTreatments.computeIfAbsent(link.getReview().getId(), ignored -> new LinkedHashMap<>())
				.putIfAbsent(treatment.treatmentId(), treatment);
		}
		Map<Long, List<ReceivedTreatmentRes>> result = new HashMap<>();
		uniqueTreatments.forEach((reviewId, treatments) -> result.put(reviewId, new ArrayList<>(treatments.values())));
		return result;
	}
}
