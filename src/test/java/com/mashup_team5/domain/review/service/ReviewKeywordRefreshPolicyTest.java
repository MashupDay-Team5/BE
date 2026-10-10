package com.mashup_team5.domain.review.service;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewKeywordRefreshPolicyTest {

	private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 8, 12, 0);
	private final ReviewKeywordRefreshPolicy policy = new ReviewKeywordRefreshPolicy();

	@ParameterizedTest
	@ValueSource(ints = {0, 5, 9})
	@DisplayName("유효한 인증 후기가 10개 미만이면 최초 생성과 갱신을 하지 않는다")
	void skipsWhenReviewsAreInsufficient(int reviewCount) {
		assertThat(policy.shouldRefresh(reviewCount, 5, null, NOW)).isFalse();
		assertThat(policy.shouldRefresh(reviewCount, 5, NOW.minusHours(24), NOW)).isFalse();
	}

	@ParameterizedTest
	@ValueSource(ints = {10, 11})
	@DisplayName("마지막 성공 기록이 없고 후기가 10개 이상이면 최초 생성한다")
	void generatesInitiallyWithEnoughReviews(int reviewCount) {
		assertThat(policy.shouldRefresh(reviewCount, 0, null, NOW)).isTrue();
	}

	@ParameterizedTest
	@CsvSource({"0, 0", "12, 0", "13, 4", "23, 4"})
	@DisplayName("24시간 미만이고 추가 후기가 5개 미만이면 갱신하지 않는다")
	void skipsBeforeBothThresholds(long elapsedHours, long addedReviewCount) {
		assertThat(policy.shouldRefresh(10 + addedReviewCount, addedReviewCount,
			NOW.minusHours(elapsedHours), NOW)).isFalse();
	}

	@Test
	@DisplayName("정확히 24시간이 지나면 추가 후기가 없어도 갱신한다")
	void refreshesAtTwentyFourHoursWithoutNewReviews() {
		assertThat(policy.shouldRefresh(10, 0, NOW.minusHours(24), NOW)).isTrue();
	}

	@Test
	@DisplayName("24시간 직전에는 추가 후기가 없으면 갱신하지 않는다")
	void skipsJustBeforeTwentyFourHours() {
		assertThat(policy.shouldRefresh(10, 0, NOW.minusHours(24).plusNanos(1), NOW)).isFalse();
	}

	@Test
	@DisplayName("24시간이 지나지 않아도 추가 후기가 정확히 5개면 갱신한다")
	void refreshesWithFiveNewReviewsBeforeTwentyFourHours() {
		assertThat(policy.shouldRefresh(15, 5, NOW.minusHours(1), NOW)).isTrue();
	}

	@Test
	@DisplayName("24시간을 초과하면 추가 후기가 5개 미만이어도 갱신한다")
	void refreshesAfterTwentyFourHours() {
		assertThat(policy.shouldRefresh(14, 4, NOW.minusHours(25), NOW)).isTrue();
	}

	@Test
	@DisplayName("추가 후기가 5개를 초과하면 갱신한다")
	void refreshesWithMoreThanFiveNewReviews() {
		assertThat(policy.shouldRefresh(16, 6, NOW.minusHours(1), NOW)).isTrue();
	}

	@Test
	@DisplayName("성공한 갱신 시점과 추가 후기 기준이 초기화되면 다시 갱신하지 않는다")
	void skipsAfterSuccessfulRefresh() {
		assertThat(policy.shouldRefresh(15, 0, NOW, NOW)).isFalse();
	}
}
