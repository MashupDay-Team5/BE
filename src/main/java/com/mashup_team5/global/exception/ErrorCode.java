package com.mashup_team5.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 리소스를 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "지원하지 않는 HTTP 메서드입니다."),
	NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "COMMON406", "요청한 응답 형식을 지원하지 않습니다."),
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON415", "지원하지 않는 요청 형식입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),

	// OpenAI
	OPENAI_NOT_CONFIGURED(HttpStatus.SERVICE_UNAVAILABLE, "OPENAI_NOT_CONFIGURED", "OpenAI API 설정이 필요합니다."),
	OPENAI_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "OPENAI_REQUEST_FAILED", "리뷰 키워드 생성 요청에 실패했습니다."),
	OPENAI_INVALID_RESPONSE(HttpStatus.BAD_GATEWAY, "OPENAI_INVALID_RESPONSE", "리뷰 키워드 생성 결과가 올바르지 않습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
