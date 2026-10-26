package com.mashup_team5.domain.hospital.dto;

import java.util.List;

public record HospitalListResponse(
	List<HospitalResponse> content,
	int page,
	int size,
	boolean hasNext
) {
}
