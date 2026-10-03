package com.mashup_team5.global.exception;

import com.mashup_team5.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		if (errorCode.getHttpStatus().is5xxServerError()) {
			log.error("서버 오류: {}", errorCode.getCode(), exception);
		}
		return ResponseEntity.status(errorCode.getHttpStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(failure(errorCode));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
		log.error("처리되지 않은 서버 오류", exception);
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity.status(errorCode.getHttpStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(failure(errorCode));
	}

	@Override
	protected @Nullable ResponseEntity<Object> handleExceptionInternal(
		Exception exception, @Nullable Object body, HttpHeaders headers,
		HttpStatusCode statusCode, WebRequest request) {

		if (statusCode.is5xxServerError()) {
			log.error("Spring MVC 서버 오류: {}", statusCode.value(), exception);
		}
		return super.handleExceptionInternal(exception, body, headers, statusCode, request);
	}

	@Override
	protected ResponseEntity<Object> createResponseEntity(
		@Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.putAll(headers);
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(failureForStatus(statusCode), responseHeaders, statusCode);
	}

	private ApiResponse<Void> failureForStatus(HttpStatusCode statusCode) {
		if (statusCode.is5xxServerError()) {
			return failure(ErrorCode.INTERNAL_SERVER_ERROR);
		}
		return switch (statusCode.value()) {
			case 400 -> failure(ErrorCode.BAD_REQUEST);
			case 404 -> failure(ErrorCode.NOT_FOUND);
			case 405 -> failure(ErrorCode.METHOD_NOT_ALLOWED);
			case 406 -> failure(ErrorCode.NOT_ACCEPTABLE);
			case 415 -> failure(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
			default -> ApiResponse.onFailure("COMMON" + statusCode.value(), "요청을 처리할 수 없습니다.", null);
		};
	}

	private ApiResponse<Void> failure(ErrorCode errorCode) {
		return ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null);
	}
}
