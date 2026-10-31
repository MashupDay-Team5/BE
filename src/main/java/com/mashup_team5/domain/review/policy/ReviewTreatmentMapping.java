package com.mashup_team5.domain.review.policy;

import java.util.List;
import java.util.Map;

public final class ReviewTreatmentMapping {

	private static final Map<Long, List<Long>> REVIEW_TREATMENT_IDS = Map.of(
		// 노안라식(40-50대) 조회 시 다음 시술을 받은 후기를 관련 후기로 포함한다.
		// 노안라식(40-50대)(14), 모노비전 라식(578), LBV 라식(579), 노안 라식(580)
		14L, List.of(14L, 578L, 579L, 580L),
		// 크라운 조회 시 크라운(33), 지르코니아 크라운(585) 후기를 관련 후기로 포함한다.
		33L, List.of(33L, 585L)
	);

	private ReviewTreatmentMapping() {
	}

	public static List<Long> treatmentIdsFor(Long treatmentId) {
		// 매핑이 없는 시술은 요청한 시술 ID만 관련 후기의 조회 대상으로 사용한다.
		return REVIEW_TREATMENT_IDS.getOrDefault(treatmentId, List.of(treatmentId));
	}
}
