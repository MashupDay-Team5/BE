package com.mashup_team5.domain.region.controller;

import com.mashup_team5.domain.region.dto.RegionResponse;
import com.mashup_team5.domain.region.service.RegionService;
import com.mashup_team5.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
@Tag(name = "Region", description = "지역 선택지 관련 API")
public class RegionController {

	private final RegionService regionService;

	@GetMapping
	@Operation(summary = "지역 선택지 조회 API", description = "parentId가 없으면 시/도 목록을, 있으면 해당 지역의 하위 지역 목록을 조회합니다.")
	public ApiResponse<List<RegionResponse>> getRegions(@RequestParam(required = false) Long parentId) {
		return ApiResponse.onSuccess(regionService.getRegions(parentId));
	}
}
