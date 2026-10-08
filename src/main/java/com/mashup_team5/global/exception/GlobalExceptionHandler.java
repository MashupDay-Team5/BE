package com.mashup_team5.global.exception;

import com.mashup_team5.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		if (errorCode.getHttpStatus().is5xxServerError()) {
			log.error("서버 오류: {}", errorCode.getCode(), exception);
		}
		return errorResponse(errorCode);
	}

	@ExceptionHandler({
		MethodArgumentNotValidException.class,
		MethodArgumentTypeMismatchException.class,
		MissingServletRequestParameterException.class,
		HandlerMethodValidationException.class,
		HttpMessageNotReadableException.class
	})
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception) {
		return errorResponse(ErrorCode.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
		log.error("처리되지 않은 서버 오류", exception);
		return errorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ApiResponse<Void>> errorResponse(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getHttpStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null));
	}
}
