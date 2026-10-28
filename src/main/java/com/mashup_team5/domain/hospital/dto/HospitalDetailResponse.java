package com.mashup_team5.domain.hospital.dto;

public record HospitalDetailResponse(
	Long hospitalId,
	String hospitalName,
	String address,
	HospitalRegionResponse region,
	double rating,
	long totalReviewCount,
	long relatedReviewCount,
	long otherReviewCount,
	TreatmentDetailResponse treatment
) {

	public record TreatmentDetailResponse(
		Long treatmentId,
		String treatmentName,
		Integer regularPrice,
		Integer discountPrice
	) {
	}
}
