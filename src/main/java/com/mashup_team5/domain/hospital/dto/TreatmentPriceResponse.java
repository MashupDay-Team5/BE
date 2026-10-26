package com.mashup_team5.domain.hospital.dto;

import com.mashup_team5.domain.hospital.enums.PriceType;

public record TreatmentPriceResponse(
        Long hospitalTreatmentId,
        Long treatmentId,
        String treatmentName,
        Integer regularPrice,
        Integer discountPrice,
        Integer displayPrice,
        PriceType priceType,
        Boolean isSpecialOffer
) {
}
