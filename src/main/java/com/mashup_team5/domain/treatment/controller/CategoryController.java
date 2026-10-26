package com.mashup_team5.domain.treatment.controller;

import com.mashup_team5.domain.treatment.dto.CategoryResponse;
import com.mashup_team5.domain.treatment.service.CategoryService;
import com.mashup_team5.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "진료 선택지 관련 API")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	@Operation(summary = "진료 선택지 조회 API", description = "진료과목, 시술카테고리, 시술 목록을 계층 구조로 조회합니다.")
	public ApiResponse<List<CategoryResponse>> getCategories() {
		return ApiResponse.onSuccess(categoryService.getCategories());
	}
}
