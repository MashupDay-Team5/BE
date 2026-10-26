package com.mashup_team5.domain.hospital.dto;

import java.util.List;

public record HospitalResponse(
	Long hospitalId,
	String name,
	HospitalRegionResponse region,
	double rating,
	long reviewCount,
	Boolean hasSpecialist,
	Boolean hasPublicPrice,
	Boolean hasNightClinic,
	Boolean hasHolidayClinic,
	Boolean isReservable,
	List<TreatmentPriceResponse> treatments
) {
}
