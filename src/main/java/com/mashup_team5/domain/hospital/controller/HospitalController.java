package com.mashup_team5.domain.hospital.controller;

import com.mashup_team5.domain.hospital.dto.HospitalDetailResponse;
import com.mashup_team5.domain.hospital.dto.HospitalCountRequest;
import com.mashup_team5.domain.hospital.dto.HospitalListResponse;
import com.mashup_team5.domain.hospital.dto.HospitalSearchRequest;
import com.mashup_team5.domain.hospital.service.HospitalService;
import com.mashup_team5.domain.hospital.enums.HospitalSort;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import com.mashup_team5.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospitals")
@Tag(name = "Hospital", description = "병원 목록 및 검색 관련 API")
public class HospitalController {

	private final HospitalService hospitalService;

	@InitBinder
	void initSearchBinder(WebDataBinder binder, NativeWebRequest request) {
		binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("true", "false", false));
		binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, false));
		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, false));
		binder.registerCustomEditor(HospitalSort.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(HospitalSort.valueOf(text));
			}
		});
		if (!binder.getObjectName().equals("hospitalSearchRequest")
				&& !binder.getObjectName().equals("hospitalCountRequest")) {
			return;
		}
		for (String field : new String[] {"treatmentIds", "regionIds"}) {
			String[] values = request.getParameterValues(field);
			if (values != null && Arrays.stream(values)
					.flatMap(value -> Arrays.stream(value.split(",", -1))).anyMatch(String::isBlank)) {
				if (field.equals("treatmentIds") && binder.getObjectName().equals("hospitalSearchRequest")
						&& "LOW_PRICE".equals(request.getParameter("sort"))
						&& Arrays.stream(values).allMatch(String::isBlank)) {
					throw new CustomException(ErrorCode.SORT_REQUIRES_TREATMENT);
				}
				throw new CustomException(ErrorCode.INVALID_FILTER);
			}
		}
		if (request.getParameterValues("treatmentId") != null) {
			throw new CustomException(ErrorCode.INVALID_FILTER);
		}
	}

	@GetMapping
	@Operation(summary = "병원 목록 조회 API", description = """
		medicalCategoryId만 전달하면 진료과목 전체, treatmentCategoryId만 전달하면 시술 카테고리 전체를 검색합니다.
		treatmentIds=301&treatmentIds=302처럼 복수 시술을 전달하면 OR 조건으로 검색합니다.
		상위·하위 조건을 함께 전달하면 모든 ID의 존재 여부와 카테고리 포함 관계를 검증합니다.
		가격은 시술별로 해당 mallId의 가격을 우선하고 없으면 정상가를 사용합니다.
		시술과 가격 범위는 같은 상품에 적용되며 응답도 해당 범위의 상품을 반환합니다.
		LOW_PRICE는 선택한 시술들의 최저 유효 가격순이며 treatmentIds 선택이 필수입니다. 동률은 병원 ID 오름차순입니다.
		"""
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
			description = "INVALID_FILTER: 잘못된 필터 / INVALID_PRICE_RANGE: 가격 범위 오류 / INVALID_PAGINATION: 페이지 오류 / INVALID_SORT: 정렬 오류 / SORT_REQUIRES_TREATMENT: LOW_PRICE 시술 미선택 / CATEGORY_MISMATCH: 포함 관계 불일치",
			content = @Content(schema = @Schema(implementation = ApiResponse.class))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
			description = "MALL_NOT_FOUND / CATEGORY_NOT_FOUND / REGION_NOT_FOUND: 선택한 ID가 존재하지 않음",
			content = @Content(schema = @Schema(implementation = ApiResponse.class))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR",
			content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	public ApiResponse<HospitalListResponse> getHospitals(
		@ParameterObject @Valid @ModelAttribute HospitalSearchRequest request) {
		return ApiResponse.onSuccess(hospitalService.getHospitals(request));
	}

	@GetMapping("/count")
	@Operation(summary = "병원 결과 수 조회 API", description = """
		병원 목록과 동일한 필터 및 카테고리 검증을 적용합니다. page, size, sort는 무시합니다.
		medicalCategoryId만 전달하면 진료과목 전체, treatmentCategoryId만 전달하면 시술 카테고리 전체를 검색합니다.
		treatmentIds=301&treatmentIds=302 형식의 복수 시술은 OR 조건입니다.
		선택한 시술별로 해당 mallId의 가격을 우선하고 없으면 정상가를 사용하여 가격 범위를 적용합니다.
		"""
	)
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
			description = "INVALID_FILTER / INVALID_PRICE_RANGE / CATEGORY_MISMATCH",
			content = @Content(schema = @Schema(implementation = ApiResponse.class))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
			description = "MALL_NOT_FOUND / CATEGORY_NOT_FOUND / REGION_NOT_FOUND",
			content = @Content(schema = @Schema(implementation = ApiResponse.class))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR",
			content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	public ApiResponse<Long> countHospitals(
		@ParameterObject @Valid @ModelAttribute HospitalCountRequest request) {
		return ApiResponse.onSuccess(hospitalService.countHospitals(request));
	}

	@GetMapping("/{hospitalId}/treatments/{treatmentId}")
	@Operation(summary = "병원·시술 상세 조회 API", description = "병원에 저장된 평점·전체 리뷰 수와 실제 시술 관련 리뷰 수를 반환합니다. mallId를 생략하거나 유효한 mallId에 해당 병원·시술의 몰 가격이 없으면 200 OK로 regularPrice와 나머지 상세 정보를 반환하며 discountPrice=null입니다. 해당 상품의 몰 가격이 있으면 discountPrice로 반환합니다. 존재하지 않는 mallId는 MALL_NOT_FOUND를 반환합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "INVALID_ID: 잘못된 병원·시술 또는 몰 ID",
			content = @Content(schema = @Schema(implementation = ApiResponse.class))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
			description = "HOSPITAL_TREATMENT_NOT_FOUND: 병원·시술 조합 없음 / MALL_NOT_FOUND: 몰 없음",
			content = @Content(schema = @Schema(implementation = ApiResponse.class))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR",
			content = @Content(schema = @Schema(implementation = ApiResponse.class)))
	})
	public ApiResponse<HospitalDetailResponse> getHospitalDetail(
		@Parameter(description = "병원 ID", schema = @Schema(type = "integer", format = "int64", minimum = "1"))
		@PathVariable("hospitalId") Long hospitalId,
		@Parameter(description = "시술 ID", schema = @Schema(type = "integer", format = "int64", minimum = "1"))
		@PathVariable("treatmentId") Long treatmentId,
		@Parameter(description = "할인 가격을 조회할 몰 ID", schema = @Schema(type = "integer", format = "int64", minimum = "1"))
		@RequestParam(value = "mallId", required = false) @Positive Long mallId) {
		return ApiResponse.onSuccess(hospitalService.getHospitalDetail(hospitalId, treatmentId, mallId));
	}
}
