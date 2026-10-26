package com.mashup_team5.domain.hospital.dto;

public record HospitalRegionResponse(
	Long sidoId,
	String sidoName,
	Long sigunguId,
	String sigunguName,
	Long dongId,
	String dongName
) {
}
