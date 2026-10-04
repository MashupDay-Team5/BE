package com.mashup_team5.global.exception;

import java.util.Objects;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(Objects.requireNonNull(errorCode, "errorCode must not be null").getMessage());
		this.errorCode = errorCode;
	}
}
