package com.mashup_team5.domain.hospital.dto;

import com.mashup_team5.domain.hospital.enums.HospitalSort;
import com.mashup_team5.domain.hospital.repository.HospitalSearchCondition;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "병원 결과 수의 필터 조건. page, size, sort는 사용하지 않습니다.")
public record HospitalCountRequest(
	@Schema(description = "진료과목 ID. 하위 조건을 생략하면 해당 진료과목의 모든 시술을 검색합니다.", minimum = "1", example = "1")
	@Positive Long medicalCategoryId,
	@Schema(description = "시술 카테고리 ID. treatmentIds를 생략하면 해당 카테고리의 모든 시술을 검색합니다. 상위 조건과 포함 관계를 검증합니다.", minimum = "1", example = "1")
	@Positive Long treatmentCategoryId,
	@ArraySchema(arraySchema = @Schema(description = "시술 ID 목록. treatmentIds=301&treatmentIds=302 형식의 OR 조건이며 상위 카테고리와 포함 관계를 검증합니다."),
		schema = @Schema(type = "integer", format = "int64", minimum = "1", example = "301"), minItems = 1)
	@Size(min = 1) List<@NotNull @Positive Long> treatmentIds,
	@ArraySchema(arraySchema = @Schema(description = "지역 ID 목록. 최대 5개, OR 조건입니다."),
		schema = @Schema(type = "integer", format = "int64", minimum = "1", example = "1"), maxItems = 5)
	@Size(min = 1, max = 5) List<@NotNull @Positive Long> regionIds,
	@Schema(description = "최소 유효 가격", minimum = "0", maximum = "15000000", example = "1000000")
	@PositiveOrZero @Max(15000000) Integer minPrice,
	@Schema(description = "최대 유효 가격", minimum = "0", maximum = "15000000", example = "2000000")
	@PositiveOrZero @Max(15000000) Integer maxPrice,
	@Schema(description = "true이면 전문의가 있는 병원만 검색합니다.")
	Boolean hasSpecialist,
	@Schema(description = "true이면 가격을 공개한 병원만 검색합니다.")
	Boolean hasPublicPrice,
	@Schema(description = "true이면 야간진료 병원만 검색합니다.")
	Boolean hasNightClinic,
	@Schema(description = "true이면 휴일진료 병원만 검색합니다.")
	Boolean hasHolidayClinic,
	@Schema(description = "몰 ID. 해당 몰의 시술 가격이 없으면 정상가를 사용합니다.", minimum = "1", example = "1")
	@Positive Long mallId
) {
	public HospitalSearchCondition toCondition() {
		return new HospitalSearchCondition(
			medicalCategoryId, treatmentCategoryId, treatmentIds, regionIds, minPrice, maxPrice,
			hasSpecialist, hasPublicPrice, hasNightClinic, hasHolidayClinic, mallId, HospitalSort.VISIT, 0, 10);
	}
}
