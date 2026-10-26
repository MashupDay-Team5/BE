package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.enums.HospitalSort;

import java.util.List;

public record HospitalSearchCondition(
        Long treatmentCategoryId,
        Long treatmentId,
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
}
