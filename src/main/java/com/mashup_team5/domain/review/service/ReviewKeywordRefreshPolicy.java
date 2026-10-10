package com.mashup_team5.domain.review.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ReviewKeywordRefreshPolicy {

	static final int MIN_REVIEW_COUNT = 10;
	private static final int MIN_NEW_REVIEW_COUNT = 5;
	private static final int REFRESH_INTERVAL_HOURS = 24;

	// 후기 개수는 같은 병원/시술의 유효한 영수증 인증 후기만 집계한다.
	// addedReviewCount와 lastGeneratedAt은 마지막 성공한 갱신을 기준으로 전달한다.
	public boolean shouldRefresh(long reviewCount, long addedReviewCount,
		LocalDateTime lastGeneratedAt, LocalDateTime now) {
		if (reviewCount < MIN_REVIEW_COUNT) {
			return false;
		}
		return lastGeneratedAt == null
			|| addedReviewCount >= MIN_NEW_REVIEW_COUNT
			|| !now.isBefore(lastGeneratedAt.plusHours(REFRESH_INTERVAL_HOURS));
	}
}
