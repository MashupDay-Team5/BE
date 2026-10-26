package com.mashup_team5.domain.hospital.controller;

import com.mashup_team5.domain.hospital.dto.HospitalListResponse;
import com.mashup_team5.domain.hospital.dto.HospitalSearchRequest;
import com.mashup_team5.domain.hospital.service.HospitalService;
import com.mashup_team5.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospitals")
@Tag(name = "Hospital", description = "병원 목록 및 검색 관련 API")
public class HospitalController {

	private final HospitalService hospitalService;

	@GetMapping
	@Operation(summary = "병원 목록 조회 API", description = "필터, 정렬, 페이지 조건에 맞는 병원 목록을 조회합니다.")
	public ApiResponse<HospitalListResponse> getHospitals(
		@ParameterObject @Valid @ModelAttribute HospitalSearchRequest request) {
		return ApiResponse.onSuccess(hospitalService.getHospitals(request));
	}

	@GetMapping("/count")
	@Operation(summary = "병원 결과 수 조회 API", description = "병원 목록과 동일한 필터 조건에 맞는 병원 수를 조회합니다.")
	public ApiResponse<Long> countHospitals(
		@ParameterObject @Valid @ModelAttribute HospitalSearchRequest request) {
		return ApiResponse.onSuccess(hospitalService.countHospitals(request));
	}
}
