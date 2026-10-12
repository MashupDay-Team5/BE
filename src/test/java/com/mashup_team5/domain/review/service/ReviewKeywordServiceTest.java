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
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
	private ReviewKeywordSourceService sourceService;

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
	@DisplayName("유효한 인증 후기가 10개면 키워드와 마지막 성공 시각·분석 후기 ID를 저장한다")
	void savesGeneratedKeywords() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요", "설명이 자세해요"));

		assertThat(service.refreshIfNeeded(target)).isTrue();
		ReviewKeyword saved = findState();
		assertThat(saved.getKeywords()).containsExactly("친절해요", "설명이 자세해요");
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getUpdatedAt()).isEqualTo(saved.getCreatedAt());
		assertThat(saved.getLastAnalyzedReviewId()).isEqualTo(lastReviewId());
		assertThat(countResults()).isEqualTo(1);
	}

	@Test
	@DisplayName("기존 결과 한 행을 수정하고 최초 생성 시각과 ID를 유지한다")
	void replacesExistingKeywordsIncludingRepeatedWords() {
		createReviews(10);
		createKeywords(target, List.of("친절해요", "대기가 길어요"));
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(25), lastReviewId());
		createReviews(1);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요", "설명이 자세해요"));

		assertThat(service.refreshIfNeeded(target)).isTrue();
		ReviewKeyword saved = findState();
		assertThat(saved.getKeywords()).containsExactly("친절해요", "설명이 자세해요");
		assertThat(saved.getId()).isEqualTo(previousState.getId());
		assertThat(saved.getCreatedAt()).isEqualTo(previousState.getCreatedAt());
		assertThat(saved.getUpdatedAt()).isAfter(previousState.getUpdatedAt());
		assertThat(saved.getLastAnalyzedReviewId()).isEqualTo(lastReviewId());
		assertThat(countResults()).isEqualTo(1);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 9})
	@DisplayName("유효한 인증 후기가 10개 미만이면 추출하지 않고 기존 키워드를 유지한다")
	void skipsGenerationWhenReviewsAreInsufficient(int count) {
		createReviews(count);
		ReviewKeyword previousState = createKeywords(target, List.of("기존 키워드"));

		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertThat(findKeywords(target)).containsExactly("기존 키워드");
		assertStateUnchanged(previousState);
		verifyNoInteractions(extractionService);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 9})
	@DisplayName("최초 생성에 필요한 후기가 부족하면 결과 행도 생성하지 않는다")
	void skipsInitialGenerationWhenReviewsAreInsufficient(int count) {
		createReviews(count);

		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertStateNotGenerated();
		verifyNoInteractions(extractionService);
	}

	@Test
	@DisplayName("24시간이 지나도 유효 후기가 10개 미만이면 기존 키워드와 갱신 기록을 유지한다")
	void keepsStateWhenReviewsFallBelowMinimum() {
		createReviews(4);
		createKeywords(target, List.of("기존 키워드"));
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(25), lastReviewId());
		createReviews(5);

		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertThat(findKeywords(target)).containsExactly("기존 키워드");
		assertStateUnchanged(previousState);
		verifyNoInteractions(extractionService);
	}

	@Test
	@DisplayName("성공한 빈 결과는 기존 행에 빈 배열로 저장하고 반복 생성을 막는다")
	void clearsExistingKeywordsAfterSuccessfulEmptyResult() {
		createReviews(10);
		ReviewKeyword previousState = createKeywords(target, List.of("기존 키워드"));
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of());

		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findKeywords(target)).isEmpty();
		assertThat(findState().getId()).isEqualTo(previousState.getId());
		assertThat(findState().getCreatedAt()).isEqualTo(previousState.getCreatedAt());
		assertThat(findState().getUpdatedAt()).isAfter(previousState.getUpdatedAt());
		assertThat(findState().getLastAnalyzedReviewId()).isEqualTo(lastReviewId());
		assertThat(countResults()).isEqualTo(1);
		assertThat(service.refreshIfNeeded(target)).isFalse();
		verify(extractionService, times(1)).extractKeywords(anyList());
	}

	@Test
	@DisplayName("최초 빈 결과도 한 행으로 저장하고 24시간 이후 새 후기가 없으면 재호출하지 않는다")
	void savesInitialEmptyResultAndSkipsWithoutNewReviews() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of());

		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findState().getKeywords()).isEmpty();
		assertThat(countResults()).isEqualTo(1);
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(25), lastReviewId());
		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertStateUnchanged(previousState);
		verify(extractionService, times(1)).extractKeywords(anyList());
	}

	@ParameterizedTest
	@EnumSource(value = ErrorCode.class,
		names = {"OPENAI_NOT_CONFIGURED", "OPENAI_REQUEST_FAILED", "OPENAI_INVALID_RESPONSE"})
	@DisplayName("추출 실패 시 기존 키워드와 갱신 기록을 유지하고 오류를 전달한다")
	void keepsExistingKeywordsWhenExtractionFails(ErrorCode errorCode) {
		createReviews(10);
		createKeywords(target, List.of("기존 키워드"));
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(25), lastReviewId());
		createReviews(1);
		CustomException exception = new CustomException(errorCode);
		when(extractionService.extractKeywords(anyList())).thenThrow(exception);

		assertThatThrownBy(() -> service.refreshIfNeeded(target)).isSameAs(exception);
		assertThat(findKeywords(target)).containsExactly("기존 키워드");
		assertStateUnchanged(previousState);
	}

	@Test
	@DisplayName("최초 추출에 실패하면 성공 기록을 만들지 않아 이후 재시도할 수 있다")
	void doesNotCreateStateAfterInitialExtractionFailure() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenThrow(new CustomException(ErrorCode.OPENAI_REQUEST_FAILED))
			.thenReturn(List.of("친절해요"));

		assertThatThrownBy(() -> service.refreshIfNeeded(target)).isInstanceOf(CustomException.class);
		assertStateNotGenerated();
		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findState().getLastAnalyzedReviewId()).isEqualTo(lastReviewId());
	}

	@Test
	@DisplayName("저장 제약 위반 시 키워드 배열·갱신 시각·분석 기준을 함께 롤백한다")
	void rollsBackReplacementWhenSavingFails() {
		createReviews(10);
		createKeywords(target, List.of("기존 키워드"));
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(25), lastReviewId());
		createReviews(1);
		when(extractionService.extractKeywords(anyList())).thenReturn(Collections.nCopies(7, "새 키워드"));

		assertThatThrownBy(() -> service.refreshIfNeeded(target)).isInstanceOf(ConstraintViolationException.class);
		assertThat(findKeywords(target)).containsExactly("기존 키워드");
		assertStateUnchanged(previousState);
	}

	@Test
	@DisplayName("최초 저장 실패 시 키워드 결과 행을 남기지 않는다")
	void rollsBackInitialStateWhenSavingFails() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(Collections.nCopies(7, "새 키워드"));

		assertThatThrownBy(() -> service.refreshIfNeeded(target)).isInstanceOf(ConstraintViolationException.class);
		assertThat(findKeywords(target)).isEmpty();
		assertStateNotGenerated();
	}

	@Test
	@DisplayName("동일 병원·시술의 결과를 중복 저장하면 UNIQUE 제약으로 거부한다")
	void rejectsDuplicateResultsForSameHospitalTreatment() {
		ReviewKeyword previousState = createKeywords(target, List.of("기존 키워드"));
		ReviewKeyword duplicate = ReviewKeyword.builder().hospitalTreatment(target).keywords(List.of("다른 키워드"))
			.lastAnalyzedReviewId(0L).createdAt(NOW).updatedAt(NOW).build();

		assertThatThrownBy(() -> repository.saveAndFlush(duplicate)).isInstanceOf(DataIntegrityViolationException.class);
		assertStateUnchanged(previousState);
		assertThat(countResults()).isEqualTo(1);
	}

	@Test
	@DisplayName("최대 6개 키워드를 순서대로 JSON 배열에 저장하고 다시 읽는다")
	void savesSixKeywordsInOrder() {
		createReviews(10);
		List<String> keywords = List.of("친절해요", "설명이 자세해요", "시설이 깨끗해요", "대기가 길어요", "가격이 합리적이에요", "재방문하고 싶어요");
		when(extractionService.extractKeywords(anyList())).thenReturn(keywords);

		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findState().getKeywords()).containsExactlyElementsOf(keywords);
		assertThat(countResults()).isEqualTo(1);
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

		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findKeywords(target)).containsExactly("새 키워드");
		assertThat(findKeywords(otherTarget)).containsExactly("다른 병원 키워드");
	}

	@Test
	@DisplayName("성공 직후 다시 요청하면 갱신을 생략하고 기존 키워드를 유지한다")
	void skipsRepeatedGenerationAfterSuccess() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요", "설명이 자세해요"));

		assertThat(service.refreshIfNeeded(target)).isTrue();
		ReviewKeyword previousState = findState();
		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertThat(findKeywords(target)).containsExactly("친절해요", "설명이 자세해요");
		assertStateUnchanged(previousState);
		verify(extractionService, times(1)).extractKeywords(anyList());
	}

	@ParameterizedTest
	@ValueSource(ints = {24, 25, 48, 168})
	@DisplayName("24시간 이상 지나도 새 후기가 없으면 추출하지 않고 키워드·갱신 기록을 유지한다")
	void skipsAfterTwentyFourHoursWithoutNewReviews(int elapsedHours) {
		createReviews(10);
		createKeywords(target, List.of("기존 키워드"));
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(elapsedHours), lastReviewId());

		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertThat(findKeywords(target)).containsExactly("기존 키워드");
		assertStateUnchanged(previousState);
		verifyNoInteractions(extractionService);
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 4})
	@DisplayName("24시간이 지나고 새 유효 후기가 1~4개면 갱신하고 성공 기록을 변경한다")
	void refreshesAfterTwentyFourHoursWithNewReviews(int addedCount) {
		createReviews(10);
		createKeywords(target, List.of("기존 키워드"));
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(24), lastReviewId());
		createReviews(addedCount);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("새 키워드"));

		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findKeywords(target)).containsExactly("새 키워드");
		assertThat(findState().getUpdatedAt()).isAfter(previousState.getUpdatedAt());
		assertThat(findState().getLastAnalyzedReviewId()).isEqualTo(lastReviewId())
			.isGreaterThan(previousState.getLastAnalyzedReviewId());
		verify(extractionService, times(1)).extractKeywords(anyList());
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 1, 4})
	@DisplayName("24시간 미만이고 새 유효 후기가 5개 미만이면 갱신하지 않는다")
	void skipsWithFewerThanFiveNewReviews(int addedCount) {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요"));
		service.refreshIfNeeded(target);
		ReviewKeyword previousState = findState();
		createReviews(addedCount);

		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertStateUnchanged(previousState);
		verify(extractionService, times(1)).extractKeywords(anyList());
	}

	@Test
	@DisplayName("24시간 미만이어도 새 유효 후기가 5개면 갱신하고 분석 기준을 변경한다")
	void refreshesWithFiveNewReviews() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("기존 키워드"))
			.thenReturn(List.of("새 키워드"));
		service.refreshIfNeeded(target);
		ReviewKeyword previousState = findState();
		createReviews(5);

		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findKeywords(target)).containsExactly("새 키워드");
		assertThat(findState().getId()).isEqualTo(previousState.getId());
		assertThat(findState().getLastAnalyzedReviewId()).isEqualTo(lastReviewId())
			.isGreaterThan(previousState.getLastAnalyzedReviewId());
		assertThat(service.refreshIfNeeded(target)).isFalse();
		verify(extractionService, times(2)).extractKeywords(anyList());
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 24, 48})
	@DisplayName("새 후기가 5개여도 영수증 인증되지 않았다면 추가분에 포함하지 않는다")
	void excludesUnverifiedNewReviews(int elapsedHours) {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("친절해요"));
		service.refreshIfNeeded(target);
		ReviewKeyword previousState = createState(LocalDateTime.now().minusHours(elapsedHours), lastReviewId());
		createReviews(5, false);

		assertThat(service.refreshIfNeeded(target)).isFalse();
		assertStateUnchanged(previousState);
		verify(extractionService, times(1)).extractKeywords(anyList());
	}

	@Test
	@DisplayName("기존 후기 5개가 삭제돼 총개수가 같아도 새 후기 5개 추가를 판단한다")
	void countsNewReviewsIndependentlyOfDeletedReviews() {
		createReviews(15);
		when(extractionService.extractKeywords(anyList())).thenReturn(List.of("기존 키워드"))
			.thenReturn(List.of("새 키워드"));
		service.refreshIfNeeded(target);
		List<Long> deletedIds = sourceService.findEligibleReviews(target.getHospital().getId(),
			target.getTreatment().getId()).stream().map(Review::getId).sorted(Comparator.reverseOrder())
			.limit(5).toList();
		transactionTemplate.executeWithoutResult(status -> {
			entityManager.createQuery("delete from ReviewTreatment rt where rt.review.id in :ids")
				.setParameter("ids", deletedIds).executeUpdate();
			entityManager.createQuery("delete from Review r where r.id in :ids")
				.setParameter("ids", deletedIds).executeUpdate();
		});
		createReviews(5);

		assertThat(sourceService.findEligibleReviews(target.getHospital().getId(), target.getTreatment().getId()))
			.hasSize(15);
		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findKeywords(target)).containsExactly("새 키워드");
		verify(extractionService, times(2)).extractKeywords(anyList());
	}

	@Test
	@DisplayName("OpenAI 처리 중 추가된 후기는 현재 분석 기준에 포함하지 않아 다음 갱신에서 반영한다")
	void keepsSnapshotBoundaryForReviewsAddedDuringExtraction() {
		createReviews(10);
		Long analyzedReviewId = lastReviewId();
		when(extractionService.extractKeywords(anyList())).thenAnswer(invocation -> {
			assertThat(invocation.<List<String>>getArgument(0)).hasSize(10);
			createReviews(5);
			return List.of("기존 키워드");
		}).thenReturn(List.of("새 키워드"));

		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findState().getLastAnalyzedReviewId()).isEqualTo(analyzedReviewId);
		assertThat(service.refreshIfNeeded(target)).isTrue();
		assertThat(findState().getLastAnalyzedReviewId()).isEqualTo(lastReviewId());
		verify(extractionService, times(2)).extractKeywords(anyList());
	}

	@Test
	@DisplayName("키워드 처리 중 변경된 정상가와 특가 여부는 덮어쓰지 않는다")
	void preservesHospitalTreatmentFieldsChangedDuringExtraction() {
		createReviews(10);
		when(extractionService.extractKeywords(anyList())).thenAnswer(invocation -> {
			transactionTemplate.executeWithoutResult(status ->
				entityManager.createQuery("""
					update HospitalTreatment ht
					set ht.regularPrice = 150000, ht.isSpecialOffer = true
					where ht.id = :id
					""").setParameter("id", target.getId()).executeUpdate());
			return List.of("친절해요");
		});

		assertThat(service.refreshIfNeeded(target)).isTrue();
		HospitalTreatment saved = transactionTemplate.execute(status ->
			entityManager.find(HospitalTreatment.class, target.getId()));
		assertThat(saved.getRegularPrice()).isEqualTo(150000);
		assertThat(saved.getIsSpecialOffer()).isTrue();
		assertThat(findState().getUpdatedAt()).isNotNull();
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
			assertThat(service.refreshIfNeeded(target)).isTrue();
		});
		assertThat(findKeywords(target)).containsExactly("친절해요");
	}

	private void createReviews(int count) {
		createReviews(count, true);
	}

	private void createReviews(int count, boolean receiptVerified) {
		transactionTemplate.executeWithoutResult(status -> {
			for (int i = 0; i < count; i++) {
				Review review = Review.builder().hospital(target.getHospital()).content("영수증 인증 후기 " + i)
					.isReceiptVerified(receiptVerified).createdAt(NOW.plusMinutes(i)).build();
				entityManager.persist(review);
				entityManager.persist(ReviewTreatment.builder().review(review).treatment(target.getTreatment()).build());
			}
		});
	}

	private ReviewKeyword createKeywords(HospitalTreatment hospitalTreatment, List<String> keywords) {
		return repository.saveAndFlush(ReviewKeyword.builder().hospitalTreatment(hospitalTreatment).keywords(keywords)
			.lastAnalyzedReviewId(0L).createdAt(NOW.minusDays(2)).updatedAt(NOW.minusDays(1)).build());
	}

	private List<String> findKeywords(HospitalTreatment hospitalTreatment) {
		return repository.findByHospitalTreatmentId(hospitalTreatment.getId())
			.map(ReviewKeyword::getKeywords).orElse(List.of());
	}

	private ReviewKeyword createState(LocalDateTime generatedAt, Long lastAnalyzedReviewId) {
		transactionTemplate.executeWithoutResult(status -> {
			ReviewKeyword state = repository.findByHospitalTreatmentId(target.getId()).orElseThrow();
			state.updateResult(state.getKeywords(), lastAnalyzedReviewId, generatedAt);
			repository.saveAndFlush(state);
		});
		return findState();
	}

	private ReviewKeyword findState() {
		return repository.findByHospitalTreatmentId(target.getId()).orElseThrow();
	}

	private Long lastReviewId() {
		return sourceService.findEligibleReviews(target.getHospital().getId(), target.getTreatment().getId()).stream()
			.map(Review::getId).max(Long::compareTo).orElseThrow();
	}

	private void assertStateUnchanged(ReviewKeyword previousState) {
		ReviewKeyword currentState = findState();
		assertThat(currentState.getId()).isEqualTo(previousState.getId());
		assertThat(currentState.getKeywords()).isEqualTo(previousState.getKeywords());
		assertThat(currentState.getCreatedAt()).isEqualTo(previousState.getCreatedAt());
		assertThat(currentState.getUpdatedAt()).isEqualTo(previousState.getUpdatedAt());
		assertThat(currentState.getLastAnalyzedReviewId()).isEqualTo(previousState.getLastAnalyzedReviewId());
	}

	private void assertStateNotGenerated() {
		assertThat(repository.findByHospitalTreatmentId(target.getId())).isEmpty();
	}

	private long countResults() {
		return entityManager.createQuery("select count(k) from ReviewKeyword k where k.hospitalTreatment.id = :id", Long.class)
			.setParameter("id", target.getId()).getSingleResult();
	}
}
