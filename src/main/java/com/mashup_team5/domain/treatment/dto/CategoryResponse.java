package com.mashup_team5.domain.treatment.dto;

import java.util.List;

public record CategoryResponse(
        Long medicalCategoryId,
        String name,
        List<TreatmentCategoryResponse> treatmentCategories
) {

    public record TreatmentCategoryResponse(
            Long treatmentCategoryId,
            String name,
            List<TreatmentResponse> treatments
    ) {
    }

    public record TreatmentResponse(Long treatmentId, String name) {
    }
}
