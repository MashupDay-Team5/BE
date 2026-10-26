package com.mashup_team5.domain.review.controller;

import com.mashup_team5.domain.review.dto.request.ReviewListReq;
import com.mashup_team5.domain.review.dto.response.ReviewListRes;
import com.mashup_team5.domain.review.service.ReviewService;
import com.mashup_team5.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospitals/{hospitalId}/treatments/{treatmentId}/reviews")
@Tag(name = "후기", description = "병원-시술 후기 조회")
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping
	@Operation(summary = "병원-시술 후기 목록 조회")
	public ApiResponse<ReviewListRes> getReviews(
		@Parameter(description = "병원 ID", schema = @Schema(minimum = "1")) @PathVariable("hospitalId") Long hospitalId,
		@Parameter(description = "시술 ID", schema = @Schema(minimum = "1")) @PathVariable("treatmentId") Long treatmentId,
		@ParameterObject @ModelAttribute ReviewListReq request) {

		return ApiResponse.onSuccess(reviewService.getReviews(hospitalId, treatmentId, request));
	}
}
