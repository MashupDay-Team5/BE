package com.mashup_team5.domain.treatment.controller;

import com.mashup_team5.domain.treatment.dto.CategoryResponse;
import com.mashup_team5.domain.treatment.service.CategoryService;
import com.mashup_team5.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public ApiResponse<List<CategoryResponse>> getCategories() {
		return ApiResponse.onSuccess(categoryService.getCategories());
	}
}
