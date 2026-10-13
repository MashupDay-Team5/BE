package com.mashup_team5.domain.review.scheduler;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.repository.ReviewKeywordTargetRepository;
import com.mashup_team5.domain.review.service.ReviewKeywordService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "review-keyword.refresh", name = "enabled", havingValue = "true")
public class ReviewKeywordScheduler {

	private static final PageRequest BATCH_REQUEST = PageRequest.of(0,100);

	private final ReviewKeywordTargetRepository targetRepository;
	private final ReviewKeywordService reviewKeywordService;

	@Scheduled(fixedDelay = 30, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
	public void refreshKeywords() {
		long afterId = 0;
		List<HospitalTreatment> targets = targetRepository.findNextBatch(afterId, BATCH_REQUEST);
		while (!targets.isEmpty()) {
			for (HospitalTreatment target : targets) {
				try {
					if (reviewKeywordService.refreshIfNeeded(target)) {
						log.info("리뷰 키워드 자동 갱신 완료: hospitalTreatmentId={}", target.getId());
					}
				} catch (RuntimeException exception) {
					log.warn("리뷰 키워드 자동 갱신 실패: hospitalTreatmentId={}", target.getId(), exception);
				}
			}
			afterId = targets.getLast().getId();
			targets = targetRepository.findNextBatch(afterId, BATCH_REQUEST);
		}
	}
}
