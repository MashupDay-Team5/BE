package com.mashup_team5.domain.hospital.repository.projection;

public record HospitalTreatmentPriceRow(
	Long hospitalId,
	Long hospitalTreatmentId,
	Long treatmentId,
	String treatmentName,
	Integer regularPrice
) {
}
