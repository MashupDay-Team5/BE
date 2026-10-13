package com.mashup_team5.domain.review.controller;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.repository.ReviewKeywordTargetRepository;
import com.mashup_team5.domain.review.service.ReviewKeywordService;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewKeywordController.class)
@ActiveProfiles("test")
class ReviewKeywordControllerTest {

	private static final String URL = "/api/hospitals/{hospitalId}/treatments/{treatmentId}/review-keywords/refresh";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReviewKeywordService service;

	@MockitoBean
	private ReviewKeywordTargetRepository repository;

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	@DisplayName("인증 헤더 없이 기존 갱신 메서드를 실행하고 갱신 여부를 반환한다")
	void returnsExistingRefreshResult(boolean refreshed) throws Exception {
		HospitalTreatment target = HospitalTreatment.builder().build();
		when(repository.findByHospitalIdAndTreatmentId(1L, 2L)).thenReturn(Optional.of(target));
		when(service.refreshIfNeeded(target)).thenReturn(refreshed);

		mockMvc.perform(post(URL, 1, 2))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").value(refreshed));

		verify(service).refreshIfNeeded(target);
	}

	@Test
	@DisplayName("병원·시술 연결이 없으면 갱신하지 않고 404를 반환한다")
	void rejectsMissingTarget() throws Exception {
		when(repository.findByHospitalIdAndTreatmentId(1L, 2L)).thenReturn(Optional.empty());

		mockMvc.perform(post(URL, 1, 2))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("COMMON404"));

		verifyNoInteractions(service);
	}

	@ParameterizedTest
	@CsvSource({"0,1", "1,0", "-1,1", "1,-1"})
	@DisplayName("양수가 아닌 ID는 갱신하지 않고 400을 반환한다")
	void rejectsInvalidIds(long hospitalId, long treatmentId) throws Exception {
		mockMvc.perform(post(URL, hospitalId, treatmentId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"));

		verifyNoInteractions(service, repository);
	}

	@Test
	@DisplayName("기존 갱신 메서드의 오류를 공통 오류 응답으로 전달한다")
	void returnsRefreshFailure() throws Exception {
		HospitalTreatment target = HospitalTreatment.builder().build();
		when(repository.findByHospitalIdAndTreatmentId(1L, 2L)).thenReturn(Optional.of(target));
		when(service.refreshIfNeeded(target)).thenThrow(new CustomException(ErrorCode.OPENAI_REQUEST_FAILED));

		mockMvc.perform(post(URL, 1, 2))
			.andExpect(status().isBadGateway())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("OPENAI_REQUEST_FAILED"));
	}
}
