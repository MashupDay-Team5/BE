package com.mashup_team5.domain.review.service;

import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.entity.Review;
import com.mashup_team5.domain.review.entity.ReviewKeyword;
import com.mashup_team5.domain.review.entity.ReviewTreatment;
import com.mashup_team5.domain.review.repository.ReviewKeywordRepository;
import com.mashup_team5.domain.treatment.entity.Treatment;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:review-keyword-storage;MODE=MySQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER",
	"openai.api-key=test-api-key", "openai.model=gpt-4.1-mini", "openai.timeout=30s"
})
class ReviewKeywordServiceTest {

	private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 11, 12, 0);

	@Autowired
	private ReviewKeywordService service;

	@Autowired
	private ReviewKeywordRepository repository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@MockitoBean
	private ReviewKeywordExtractionService extractionService;

	private HospitalTreatment target;

	@BeforeEach
	void setUp() {
		target = transactionTemplate.execute(status -> {
			Hospital hospital = Hospital.builder().name("테스트 병원").build();
			Treatment treatment = Treatment.builder().name("라식").build();
			entityManager.persist(hospital);
			entityManager.persist(treatment);
			HospitalTreatment hospitalTreatment = HospitalTreatment.builder()
				.hospital(hospital).treatment(treatment).build();
			entityManager.persist(hospitalTreatment);
			return hospitalTreatment;
		});
	}

	@Test
	@DisplayName("유효한 인증 후기가 10개면 추출한 키워드와 생성·수정 시각을 저장한다")
	void savesGeneratedKeywords() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요", "설명이 자세해요"));

		assertThat(service.generateAndSave(target)).isTrue();
		List<ReviewKeyword> saved = findKeywords(target);
		assertThat(saved).extracting(ReviewKeyword::getKeyword).containsExactly("친절해요", "설명이 자세해요");
		assertThat(saved).allSatisfy(keyword -> {
			assertThat(keyword.getCreatedAt()).isNotNull();
			assertThat(keyword.getUpdatedAt()).isEqualTo(keyword.getCreatedAt());
		});
	}

	@Test
	@DisplayName("기존 키워드와 일부가 같아도 충돌 없이 새 결과로 교체한다")
	void replacesExistingKeywordsIncludingRepeatedWords() {
		createReviews(10);
		createKeywords(target, List.of("친절해요", "대기가 길어요"));
		List<Long> oldIds = findKeywords(target).stream().map(ReviewKeyword::getId).toList();
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요", "설명이 자세해요"));

		assertThat(service.generateAndSave(target)).isTrue();
		List<ReviewKeyword> saved = findKeywords(target);
		assertThat(saved).extracting(ReviewKeyword::getKeyword).containsExactly("친절해요", "설명이 자세해요");
		assertThat(saved).extracting(ReviewKeyword::getId).doesNotContainAnyElementsOf(oldIds);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 9})
	@DisplayName("유효한 인증 후기가 10개 미만이면 추출하지 않고 기존 키워드를 유지한다")
	void skipsGenerationWhenReviewsAreInsufficient(int count) {
		createReviews(count);
		createKeywords(target, List.of("기존 키워드"));

		assertThat(service.generateAndSave(target)).isFalse();
		assertThat(findKeywords(target)).extracting(ReviewKeyword::getKeyword).containsExactly("기존 키워드");
		verifyNoInteractions(extractionService);
	}

	@Test
	@DisplayName("성공한 추출 결과가 빈 목록이면 기존 키워드를 제거한다")
	void clearsExistingKeywordsAfterSuccessfulEmptyResult() {
		createReviews(10);
		createKeywords(target, List.of("기존 키워드"));
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of());

		assertThat(service.generateAndSave(target)).isTrue();
		assertThat(findKeywords(target)).isEmpty();
	}

	@ParameterizedTest
	@EnumSource(value = ErrorCode.class,
		names = {"OPENAI_NOT_CONFIGURED", "OPENAI_REQUEST_FAILED", "OPENAI_INVALID_RESPONSE"})
	@DisplayName("추출 실패 시 기존 키워드를 유지하고 오류를 전달한다")
	void keepsExistingKeywordsWhenExtractionFails(ErrorCode errorCode) {
		createReviews(10);
		createKeywords(target, List.of("기존 키워드"));
		CustomException exception = new CustomException(errorCode);
		when(extractionService.extractKeywords(anyList())).thenThrow(exception);

		assertThatThrownBy(() -> service.generateAndSave(target)).isSameAs(exception);
		assertThat(findKeywords(target)).extracting(ReviewKeyword::getKeyword).containsExactly("기존 키워드");
	}

	@Test
	@DisplayName("DB 저장 도중 실패하면 삭제와 부분 저장을 롤백하여 기존 키워드를 유지한다")
	void rollsBackReplacementWhenSavingFails() {
		createReviews(10);
		createKeywords(target, List.of("기존 키워드"));
		Long oldId = findKeywords(target).getFirst().getId();
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("새 키워드", "가".repeat(256)));

		assertThatThrownBy(() -> service.generateAndSave(target)).isInstanceOf(DataIntegrityViolationException.class);
		List<ReviewKeyword> saved = findKeywords(target);
		assertThat(saved).extracting(ReviewKeyword::getKeyword).containsExactly("기존 키워드");
		assertThat(saved).extracting(ReviewKeyword::getId).containsExactly(oldId);
	}

	@Test
	@DisplayName("키워드를 교체해도 다른 병원·시술의 키워드는 변경하지 않는다")
	void leavesOtherTargetsUnchanged() {
		createReviews(10);
		HospitalTreatment otherTarget = transactionTemplate.execute(status -> {
			Hospital hospital = Hospital.builder().name("다른 병원").build();
			entityManager.persist(hospital);
			HospitalTreatment hospitalTreatment = HospitalTreatment.builder()
				.hospital(hospital).treatment(target.getTreatment()).build();
			entityManager.persist(hospitalTreatment);
			return hospitalTreatment;
		});
		createKeywords(target, List.of("기존 키워드"));
		createKeywords(otherTarget, List.of("다른 병원 키워드"));
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("새 키워드"));

		assertThat(service.generateAndSave(target)).isTrue();
		assertThat(findKeywords(target)).extracting(ReviewKeyword::getKeyword).containsExactly("새 키워드");
		assertThat(findKeywords(otherTarget)).extracting(ReviewKeyword::getKeyword).containsExactly("다른 병원 키워드");
	}

	@Test
	@DisplayName("같은 결과를 다시 저장해도 키워드가 누적되지 않는다")
	void doesNotAccumulateKeywordsAcrossRepeatedGeneration() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요", "설명이 자세해요"));

		assertThat(service.generateAndSave(target)).isTrue();
		assertThat(service.generateAndSave(target)).isTrue();
		assertThat(findKeywords(target)).extracting(ReviewKeyword::getKeyword).containsExactly("친절해요", "설명이 자세해요");
	}

	@Test
	@DisplayName("호출한 쪽에 트랜잭션이 있어도 OpenAI 추출 중에는 DB 트랜잭션을 유지하지 않는다")
	void extractsOutsideDatabaseTransaction() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenAnswer(invocation -> {
			assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
			return List.of("친절해요");
		});

		transactionTemplate.executeWithoutResult(status -> {
			assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
			assertThat(service.generateAndSave(target)).isTrue();
		});
		assertThat(findKeywords(target)).extracting(ReviewKeyword::getKeyword).containsExactly("친절해요");
	}

	private void createReviews(int count) {
		transactionTemplate.executeWithoutResult(status -> {
			for (int i = 0; i < count; i++) {
				Review review = Review.builder().hospital(target.getHospital()).content("영수증 인증 후기 " + i)
					.isReceiptVerified(true).createdAt(NOW.plusMinutes(i)).build();
				entityManager.persist(review);
				entityManager.persist(ReviewTreatment.builder().review(review).treatment(target.getTreatment()).build());
			}
		});
	}

	private void createKeywords(HospitalTreatment hospitalTreatment, List<String> keywords) {
		transactionTemplate.executeWithoutResult(status -> keywords.forEach(keyword ->
			entityManager.persist(ReviewKeyword.builder().hospitalTreatment(hospitalTreatment).keyword(keyword)
				.createdAt(NOW.minusDays(1)).updatedAt(NOW.minusDays(1)).build())));
	}

	private List<ReviewKeyword> findKeywords(HospitalTreatment hospitalTreatment) {
		return repository.findAllByHospitalTreatmentIdOrderByIdAsc(hospitalTreatment.getId());
	}
}
