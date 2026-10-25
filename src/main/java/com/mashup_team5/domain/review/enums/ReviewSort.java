package com.mashup_team5.domain.review.enums;

import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;

public enum ReviewSort {

	DEFAULT,
	LATEST,
	HIGHEST_RATING,
	LOWEST_RATING,
	MOST_HELPFUL;

	public static ReviewSort from(String value) {
		try {
			return valueOf(value);
		} catch (IllegalArgumentException | NullPointerException exception) {
			throw new CustomException(ErrorCode.INVALID_SORT);
		}
	}
}
