package com.mashup_team5.domain.review.controller;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.dto.response.ReviewKeywordRes;
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
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping
	@Operation(summary = "병원-시술 후기 키워드 조회",
		description = "DB에 저장된 키워드를 저장 순서대로 최대 6개 반환합니다. 없으면 빈 목록을 반환하며 OpenAI를 호출하지 않습니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
				description = "저장된 키워드 목록. 없으면 빈 배열 반환", useReturnTypeSchema = true),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
				description = "숫자 형식 오류: COMMON400, 1 미만의 ID: INVALID_ID400"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
				description = "병원·시술 조합 없음: HOSPITAL_TREATMENT_NOT_FOUND404"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500",
				description = "조회 실패: INTERNAL_SERVER_ERROR")
		})
	public ApiResponse<ReviewKeywordRes> getKeywords(
		@Parameter(description = "병원 ID", schema = @Schema(minimum = "1")) @PathVariable("hospitalId") Long hospitalId,
		@Parameter(description = "시술 ID", schema = @Schema(minimum = "1")) @PathVariable("treatmentId") Long treatmentId) {

		return ApiResponse.onSuccess(reviewKeywordService.getKeywords(hospitalId, treatmentId));
	}

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
