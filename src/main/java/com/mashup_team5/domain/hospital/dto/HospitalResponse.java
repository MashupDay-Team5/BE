package com.mashup_team5.domain.hospital.dto;

import com.mashup_team5.domain.hospital.enums.HospitalBadgeType;
import java.util.List;

public record HospitalResponse(
	Long hospitalId,
	String name,
	String address,
	HospitalRegionResponse region,
	double rating,
	long reviewCount,
	Boolean hasSpecialist,
	Boolean hasPublicPrice,
	Boolean hasNightClinic,
	Boolean hasHolidayClinic,
	List<HospitalBadgeType> badgeTypes,
	List<TreatmentPriceResponse> treatments
) {
}
