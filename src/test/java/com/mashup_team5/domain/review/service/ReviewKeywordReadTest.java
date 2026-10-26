package com.mashup_team5.domain.review.service;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.dto.response.ReviewKeywordRes;
import com.mashup_team5.domain.review.entity.ReviewKeyword;
import com.mashup_team5.domain.review.repository.ReviewKeywordRepository;
import com.mashup_team5.domain.review.repository.ReviewKeywordTargetRepository;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewKeywordReadTest {

	private static final Long HOSPITAL_TREATMENT_ID = 3L;

	@Mock
	private ReviewKeywordTargetRepository targetRepository;

	@Mock
	private ReviewKeywordRepository reviewKeywordRepository;

	@Mock
	private ReviewKeywordSourceService sourceService;

	@Mock
	private ReviewKeywordExtractionService extractionService;

	@Mock
	private EntityManager entityManager;

	@Mock
	private ReviewKeywordRefreshPolicy refreshPolicy;

	@Mock
	private TransactionTemplate transactionTemplate;

	@InjectMocks
	private ReviewKeywordService service;

	@AfterEach
	void doesNotInvokeGenerationOrRefreshDependencies() {
		verifyNoInteractions(sourceService, extractionService, entityManager, refreshPolicy, transactionTemplate);
	}

	@Test
	@DisplayName("선택한 병원·시술에 저장된 키워드를 저장 순서대로 조회한다")
	void returnsStoredKeywordsWithoutWriting() {
		List<String> keywords = List.of("자세한 설명", "친절한 상담");
		givenKeywords(keywords);

		ReviewKeywordRes response = service.getKeywords(1L, 2L);

		assertThat(response.hospitalId()).isEqualTo(1L);
		assertThat(response.treatmentId()).isEqualTo(2L);
		assertThat(response.keywords()).containsExactlyElementsOf(keywords);
		verify(reviewKeywordRepository).findByHospitalTreatmentId(HOSPITAL_TREATMENT_ID);
		verifyNoMoreInteractions(reviewKeywordRepository);
	}

	@Test
	@DisplayName("조합은 존재하지만 키워드가 생성되지 않았으면 빈 목록을 반환한다")
	void returnsEmptyListWhenNoResultExists() {
		givenTarget();
		when(reviewKeywordRepository.findByHospitalTreatmentId(HOSPITAL_TREATMENT_ID)).thenReturn(Optional.empty());

		assertThat(service.getKeywords(1L, 2L).keywords()).isEmpty();
	}

	@Test
	@DisplayName("저장된 키워드가 빈 배열이어도 빈 목록을 반환한다")
	void returnsStoredEmptyList() {
		givenKeywords(List.of());

		assertThat(service.getKeywords(1L, 2L).keywords()).isEmpty();
	}

	@Test
	@DisplayName("저장된 순서를 유지하면서 중복 없는 키워드를 최대 6개 반환한다")
	void returnsAtMostSixDistinctKeywords() {
		givenKeywords(List.of("설명", "상담", "설명", "대기", "시설", "가격", "친절", "접근성"));

		assertThat(service.getKeywords(1L, 2L).keywords())
			.containsExactly("설명", "상담", "대기", "시설", "가격", "친절");
	}

	@Test
	@DisplayName("병원·시술 조합이 없으면 키워드를 조회하지 않고 404 오류를 발생시킨다")
	void rejectsMissingHospitalTreatment() {
		when(targetRepository.findByHospitalIdAndTreatmentId(1L, 2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getKeywords(1L, 2L))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.HOSPITAL_TREATMENT_NOT_FOUND);
		verifyNoInteractions(reviewKeywordRepository);
	}

	@ParameterizedTest
	@CsvSource(value = {"0,2", "-1,2", "1,0", "1,-1", "NULL,2", "1,NULL"}, nullValues = "NULL")
	@DisplayName("유효하지 않은 ID는 DB 조회 전에 거부한다")
	void rejectsInvalidIds(Long hospitalId, Long treatmentId) {
		assertThatThrownBy(() -> service.getKeywords(hospitalId, treatmentId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ID);
		verifyNoInteractions(targetRepository, reviewKeywordRepository);
	}

	private void givenTarget() {
		HospitalTreatment target = mock(HospitalTreatment.class);
		when(target.getId()).thenReturn(HOSPITAL_TREATMENT_ID);
		when(targetRepository.findByHospitalIdAndTreatmentId(1L, 2L)).thenReturn(Optional.of(target));
	}

	private void givenKeywords(List<String> keywords) {
		givenTarget();
		when(reviewKeywordRepository.findByHospitalTreatmentId(HOSPITAL_TREATMENT_ID))
			.thenReturn(Optional.of(ReviewKeyword.builder().keywords(keywords).build()));
	}
}
