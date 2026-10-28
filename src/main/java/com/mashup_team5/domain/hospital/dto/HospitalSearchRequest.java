package com.mashup_team5.domain.hospital.dto;

import com.mashup_team5.domain.hospital.enums.HospitalSort;
import com.mashup_team5.domain.hospital.repository.HospitalSearchCondition;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "병원 목록의 검색, 정렬 및 페이지 조건")
public record HospitalSearchRequest(
		@Schema(description = "진료과목 ID. 하위 조건을 생략하면 해당 진료과목의 모든 시술을 검색합니다.",
				example = "1", minimum = "1")
		@Positive Long medicalCategoryId,
		@Schema(description = "시술 카테고리 ID. treatmentIds를 생략하면 해당 카테고리의 모든 시술을 검색합니다. 상위 조건과 포함 관계를 검증합니다.",
				example = "1", minimum = "1")
		@Positive Long treatmentCategoryId,
		@ArraySchema(arraySchema = @Schema(description = "시술 ID 목록. treatmentIds=301&treatmentIds=302 형식으로 전달하며 OR 조건입니다. 상위 카테고리와 포함 관계를 검증합니다. LOW_PRICE 정렬에 필수입니다."),
				schema = @Schema(type = "integer", format = "int64", example = "301", minimum = "1"), minItems = 1)
		@Size(min = 1) List<@NotNull @Positive Long> treatmentIds,
		@ArraySchema(
				arraySchema = @Schema(description = "선택한 지역 ID 목록. 최대 5개까지 선택할 수 있으며 복수 조건은 OR로 적용됩니다."),
				schema = @Schema(description = "SIDO 또는 SIGUNGU 지역 ID", example = "1", minimum = "1"), maxItems = 5)
		@Size(min = 1, max = 5) List<@NotNull @Positive Long> regionIds,
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
		@Schema(description = "정렬 기준. LOW_PRICE는 선택한 treatmentIds의 최저 유효 가격순이며 시술 선택이 필수입니다. 동률은 병원 ID 오름차순입니다. 생략하면 방문 많은 순입니다.",
				example = "VISIT", defaultValue = "VISIT")
		HospitalSort sort,
		@Schema(description = "0부터 시작하는 페이지 번호", example = "0", defaultValue = "0",
				minimum = "0", maximum = "1000000")
		@Min(0) @Max(1000000) Integer page,
		@Schema(description = "페이지당 병원 수", example = "10", defaultValue = "10",
				minimum = "1", maximum = "50")
		@Min(1) @Max(50) Integer size
) {

	public HospitalSearchRequest {
		sort = sort == null ? HospitalSort.VISIT : sort;
		page = page == null ? 0 : page;
		size = size == null ? 10 : size;
	}

	public HospitalSearchCondition toCondition() {
		return new HospitalSearchCondition(
				medicalCategoryId,
				treatmentCategoryId,
				treatmentIds,
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
