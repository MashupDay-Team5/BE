package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.enums.HospitalSort;

import java.util.List;

public record HospitalSearchCondition(
        Long medicalCategoryId,
        Long treatmentCategoryId,
        List<Long> treatmentIds,
        List<Long> regionIds,
        Integer minPrice,
        Integer maxPrice,
        Boolean hasSpecialist,
        Boolean hasPublicPrice,
        Boolean hasNightClinic,
        Boolean hasHolidayClinic,
        Long mallId,
        HospitalSort sort,
        int page,
        int size
) {
	public HospitalSearchCondition {
		treatmentIds = treatmentIds == null ? List.of() : treatmentIds.stream().distinct().toList();
		regionIds = regionIds == null ? List.of() : regionIds.stream().distinct().toList();
	}
}
