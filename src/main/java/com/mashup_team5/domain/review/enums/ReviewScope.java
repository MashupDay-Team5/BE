package com.mashup_team5.domain.review.enums;

import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;

public enum ReviewScope {

	RELATED,
	OTHER;

	public static ReviewScope from(String value) {
		try {
			return valueOf(value);
		} catch (IllegalArgumentException | NullPointerException exception) {
			throw new CustomException(ErrorCode.INVALID_REVIEW_SCOPE);
		}
	}
}
