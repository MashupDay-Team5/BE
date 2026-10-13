package com.mashup_team5.domain.review.controller;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.repository.ReviewKeywordTargetRepository;
import com.mashup_team5.domain.review.service.ReviewKeywordService;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import com.mashup_team5.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospitals/{hospitalId}/treatments/{treatmentId}/review-keywords")
@Tag(name = "후기", description = "병원-시술 후기 키워드")
public class ReviewKeywordController {

	private final ReviewKeywordService reviewKeywordService;
	private final ReviewKeywordTargetRepository targetRepository;

	@PostMapping("/refresh")
	@Operation(summary = "리뷰 키워드 수동 갱신", description = "스케줄러 활성화 여부와 관계없이 실행합니다. "
		+ "기존 갱신 조건을 적용하며, data=true는 갱신 완료, data=false는 조건 미충족으로 생략한 경우입니다.")
	public ApiResponse<Boolean> refreshKeywords(
		@Parameter(description = "병원 ID", schema = @Schema(minimum = "1")) @PathVariable("hospitalId") @Min(1) Long hospitalId,
		@Parameter(description = "시술 ID", schema = @Schema(minimum = "1")) @PathVariable("treatmentId") @Min(1) Long treatmentId) {

		HospitalTreatment target = targetRepository.findByHospitalIdAndTreatmentId(hospitalId, treatmentId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		return ApiResponse.onSuccess(reviewKeywordService.refreshIfNeeded(target));
	}
}
