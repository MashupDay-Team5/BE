package com.mashup_team5.domain.hospital.dto;

import com.mashup_team5.domain.hospital.repository.HospitalSearchCondition;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Schema(description = "병원 목록과 결과 수 조회에 공통으로 사용하는 검색 조건")
public record HospitalSearchRequest(
	@Schema(description = "현재 선택한 시술 카테고리 ID. 카테고리의 '전체' 선택 시 이 값만 전달하고 treatmentId는 생략합니다.",
		example = "1", minimum = "1")
	@Positive Long treatmentCategoryId,
	@Schema(description = "특정 시술 칩을 선택했을 때만 전달하는 시술 ID. LOW_PRICE 정렬에는 필수입니다.",
		example = "2", minimum = "1")
	@Positive Long treatmentId,
	@ArraySchema(
		arraySchema = @Schema(description = "선택한 지역 ID 목록. Add item으로 ID를 하나씩 추가하며 복수 조건은 OR로 적용됩니다."),
		schema = @Schema(description = "SIDO 또는 SIGUNGU 지역 ID", example = "1", minimum = "1")
	)
	List<@Positive Long> regionIds,
	@Schema(description = "최소 유효 가격. 생략하면 하한을 적용하지 않으며 0은 실제 0원 조건입니다.",
		example = "1000000", minimum = "0", maximum = "15000000")
	@PositiveOrZero @Max(15000000) Integer minPrice,
	@Schema(description = "최대 유효 가격. 생략하면 상한을 적용하지 않으며 0은 실제 0원 조건입니다.",
		example = "2000000", minimum = "0", maximum = "15000000")
	@PositiveOrZero @Max(15000000) Integer maxPrice,
	@Schema(description = "true이면 전문의가 있는 병원만 조회합니다. false 또는 생략은 조건을 적용하지 않습니다.",
		example = "true")
	Boolean hasSpecialist,
	@Schema(description = "true이면 가격을 공개한 병원만 조회합니다. false 또는 생략은 조건을 적용하지 않습니다.",
		example = "true")
	Boolean hasPublicPrice,
	@Schema(description = "true이면 야간진료 병원만 조회합니다. false 또는 생략은 조건을 적용하지 않습니다.",
		example = "true")
	Boolean hasNightClinic,
	@Schema(description = "true이면 휴일진료 병원만 조회합니다. false 또는 생략은 조건을 적용하지 않습니다.",
		example = "true")
	Boolean hasHolidayClinic,
	@Schema(description = "할인 가격을 조회할 몰 ID. 해당 몰 가격이 없으면 정상가를 사용하며 생략하면 정상가만 사용합니다.",
		example = "1", minimum = "1")
	@Positive Long mallId,
	@Schema(description = "정렬 기준. 생략하면 방문 많은 순입니다.",
		example = "VISIT", defaultValue = "VISIT")
	HospitalSort sort,
	@Schema(description = "0부터 시작하는 페이지 번호", example = "0", defaultValue = "0",
		minimum = "0", maximum = "1000000")
	@Min(0) @Max(1000000) Integer page,
	@Schema(description = "페이지당 병원 수", example = "10", defaultValue = "10",
		minimum = "1", maximum = "100")
	@Min(1) @Max(100) Integer size
) {

	public HospitalSearchRequest {
		regionIds = regionIds == null ? List.of() : List.copyOf(regionIds);
		sort = sort == null ? HospitalSort.VISIT : sort;
		page = page == null ? 0 : page;
		size = size == null ? 10 : size;
	}

	public HospitalSearchCondition toCondition() {
		return new HospitalSearchCondition(
			treatmentCategoryId,
			treatmentId,
			regionIds,
			minPrice,
			maxPrice,
			hasSpecialist,
			hasPublicPrice,
			hasNightClinic,
			hasHolidayClinic,
			mallId,
			sort,
			page,
			size
		);
	}
}
