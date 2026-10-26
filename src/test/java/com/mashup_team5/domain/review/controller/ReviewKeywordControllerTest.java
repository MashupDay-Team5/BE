package com.mashup_team5.domain.review.controller;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.dto.response.ReviewKeywordRes;
import com.mashup_team5.domain.review.repository.ReviewKeywordTargetRepository;
import com.mashup_team5.domain.review.service.ReviewKeywordService;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewKeywordController.class)
@ActiveProfiles("test")
class ReviewKeywordControllerTest {

	private static final String KEYWORDS_URL = "/api/hospitals/{hospitalId}/treatments/{treatmentId}/review-keywords";
	private static final String REFRESH_URL = KEYWORDS_URL + "/refresh";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReviewKeywordService service;

	@MockitoBean
	private ReviewKeywordTargetRepository repository;

	@Test
	@DisplayName("인증 없이 저장된 키워드를 공통 성공 응답으로 조회한다")
	void returnsStoredKeywords() throws Exception {
		when(service.getKeywords(1L, 2L))
			.thenReturn(new ReviewKeywordRes(1L, 2L, List.of("자세한 설명", "친절한 상담")));

		mockMvc.perform(get(KEYWORDS_URL, 1, 2))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.data.hospitalId").value(1))
			.andExpect(jsonPath("$.data.treatmentId").value(2))
			.andExpect(jsonPath("$.data.keywords").value(contains("자세한 설명", "친절한 상담")));

		verify(service).getKeywords(1L, 2L);
		verifyNoMoreInteractions(service);
		verifyNoInteractions(repository);
	}

	@Test
	@DisplayName("키워드가 없으면 null 대신 빈 배열과 200을 반환한다")
	void returnsEmptyKeywords() throws Exception {
		when(service.getKeywords(1L, 2L)).thenReturn(new ReviewKeywordRes(1L, 2L, List.of()));

		mockMvc.perform(get(KEYWORDS_URL, 1, 2))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.keywords").isArray())
			.andExpect(jsonPath("$.data.keywords").isEmpty());

		verify(service).getKeywords(1L, 2L);
		verifyNoMoreInteractions(service);
		verifyNoInteractions(repository);
	}

	@Test
	@DisplayName("조회 대상 조합이 없으면 전용 오류 코드와 404를 반환한다")
	void rejectsMissingQueryTarget() throws Exception {
		when(service.getKeywords(1L, 2L))
			.thenThrow(new CustomException(ErrorCode.HOSPITAL_TREATMENT_NOT_FOUND));

		mockMvc.perform(get(KEYWORDS_URL, 1, 2))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("HOSPITAL_TREATMENT_NOT_FOUND404"));
	}

	@Test
	@DisplayName("조회 ID의 값 검증 오류는 전용 오류 코드와 400을 반환한다")
	void returnsInvalidQueryIdError() throws Exception {
		when(service.getKeywords(0L, 2L)).thenThrow(new CustomException(ErrorCode.INVALID_ID));

		mockMvc.perform(get(KEYWORDS_URL, 0, 2))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("INVALID_ID400"));
	}

	@ParameterizedTest
	@CsvSource({
		"text,2", "1,text", "0.5,2", "1,0.5",
		"9223372036854775808,2", "1,9223372036854775808"
	})
	@DisplayName("ID 숫자 변환 실패는 기존 공통 400 응답을 사용한다")
	void rejectsMalformedQueryIds(String hospitalId, String treatmentId) throws Exception {
		mockMvc.perform(get(KEYWORDS_URL, hospitalId, treatmentId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"));

		verifyNoInteractions(service, repository);
	}

	@Test
	@DisplayName("조회 장애는 빈 결과로 숨기지 않고 기존 공통 500을 반환한다")
	void returnsQueryFailure() throws Exception {
		when(service.getKeywords(1L, 2L))
			.thenThrow(new DataAccessResourceFailureException("internal-database-detail"));

		String body = mockMvc.perform(get(KEYWORDS_URL, 1, 2))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
			.andExpect(jsonPath("$.data").value((Object) null))
			.andReturn().getResponse().getContentAsString();

		assertThat(body).doesNotContain("internal-database-detail");
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	@DisplayName("인증 헤더 없이 기존 갱신 메서드를 실행하고 갱신 여부를 반환한다")
	void returnsExistingRefreshResult(boolean refreshed) throws Exception {
		HospitalTreatment target = HospitalTreatment.builder().build();
		when(repository.findByHospitalIdAndTreatmentId(1L, 2L)).thenReturn(Optional.of(target));
		when(service.refreshIfNeeded(target)).thenReturn(refreshed);

		mockMvc.perform(post(REFRESH_URL, 1, 2))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").value(refreshed));

		verify(service).refreshIfNeeded(target);
	}

	@Test
	@DisplayName("병원·시술 연결이 없으면 갱신하지 않고 404를 반환한다")
	void rejectsMissingTarget() throws Exception {
		when(repository.findByHospitalIdAndTreatmentId(1L, 2L)).thenReturn(Optional.empty());

		mockMvc.perform(post(REFRESH_URL, 1, 2))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("COMMON404"));

		verifyNoInteractions(service);
	}

	@ParameterizedTest
	@CsvSource({"0,1", "1,0", "-1,1", "1,-1"})
	@DisplayName("양수가 아닌 ID는 갱신하지 않고 400을 반환한다")
	void rejectsInvalidIds(long hospitalId, long treatmentId) throws Exception {
		mockMvc.perform(post(REFRESH_URL, hospitalId, treatmentId))
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

		mockMvc.perform(post(REFRESH_URL, 1, 2))
			.andExpect(status().isBadGateway())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("OPENAI_REQUEST_FAILED"));
	}
}
