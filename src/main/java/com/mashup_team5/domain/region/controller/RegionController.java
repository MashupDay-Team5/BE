package com.mashup_team5.domain.region.controller;

import com.mashup_team5.domain.region.dto.RegionResponse;
import com.mashup_team5.domain.region.service.RegionService;
import com.mashup_team5.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/regions")
public class RegionController {

	private final RegionService regionService;

	@GetMapping
	public ApiResponse<List<RegionResponse>> getRegions(@RequestParam(required = false) Long parentId) {
		return ApiResponse.onSuccess(regionService.getRegions(parentId));
	}
}
