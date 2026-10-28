package com.mashup_team5.domain.hospital.controller;

import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import com.mashup_team5.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.HandlerMapping;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = HospitalController.class)
public class HospitalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException exception) {
		return errorResponse(exception.getErrorCode());
	}

	@ExceptionHandler({
		MethodArgumentNotValidException.class,
		BindException.class,
		MethodArgumentTypeMismatchException.class,
		MissingServletRequestParameterException.class,
		HandlerMethodValidationException.class
	})
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception, HttpServletRequest request) {
		if ("/api/hospitals/{hospitalId}/treatments/{treatmentId}".equals(
				request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))) {
			return errorResponse(ErrorCode.INVALID_ID);
		}
		if (exception instanceof BindException binding) {
			if (binding.getFieldErrors().stream().anyMatch(error -> error.getField().equals("sort"))) {
				return errorResponse(ErrorCode.INVALID_SORT);
			}
			if (binding.getFieldErrors().stream().anyMatch(error ->
					error.getField().equals("page") || error.getField().equals("size"))) {
				return errorResponse(ErrorCode.INVALID_PAGINATION);
			}
		}
		return errorResponse(ErrorCode.INVALID_FILTER);
	}

	private ResponseEntity<ApiResponse<Void>> errorResponse(ErrorCode errorCode) {
		String code = switch (errorCode) {
			case INVALID_ID, INVALID_SORT, INVALID_PAGINATION, INVALID_PRICE_RANGE,
				 HOSPITAL_TREATMENT_NOT_FOUND -> errorCode.name();
			default -> errorCode.getCode();
		};
		return ResponseEntity.status(errorCode.getHttpStatus())
				.contentType(MediaType.APPLICATION_JSON)
				.body(ApiResponse.onFailure(code, errorCode.getMessage(), null));
	}
}
