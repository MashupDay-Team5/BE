package com.mashup_team5.domain.review.scheduler;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.review.repository.ReviewKeywordTargetRepository;
import com.mashup_team5.domain.review.service.ReviewKeywordService;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import java.time.Duration;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewKeywordSchedulerTest {

	private static final PageRequest BATCH_REQUEST = PageRequest.of(0, 100);

	@Mock
	private ReviewKeywordTargetRepository targetRepository;

	@Mock
	private ReviewKeywordService service;

	@InjectMocks
	private ReviewKeywordScheduler scheduler;

	@Test
	@DisplayName("대상이 없으면 갱신 서비스를 호출하지 않는다")
	void skipsWhenNoTargetsExist() {
		scheduler.refreshKeywords();

		verify(targetRepository).findNextBatch(0L, BATCH_REQUEST);
		verifyNoInteractions(service);
	}

	@Test
	@DisplayName("조회 요청 없이 각 병원·시술의 갱신 조건을 검사한다")
	void checksEachTarget() {
		HospitalTreatment first = target(1L);
		HospitalTreatment second = target(2L);
		when(targetRepository.findNextBatch(0L, BATCH_REQUEST)).thenReturn(List.of(first, second));
		when(service.refreshIfNeeded(first)).thenReturn(true);

		scheduler.refreshKeywords();

		verify(service).refreshIfNeeded(first);
		verify(service).refreshIfNeeded(second);
		verify(targetRepository).findNextBatch(2L, BATCH_REQUEST);
	}

	@Test
	@DisplayName("한 대상의 OpenAI 갱신이 실패해도 다음 대상을 처리한다")
	void continuesAfterOneTargetFails() {
		HospitalTreatment first = target(1L);
		HospitalTreatment second = target(2L);
		when(targetRepository.findNextBatch(0L, BATCH_REQUEST)).thenReturn(List.of(first, second));
		when(service.refreshIfNeeded(first)).thenThrow(new CustomException(ErrorCode.OPENAI_REQUEST_FAILED));
		when(service.refreshIfNeeded(second)).thenReturn(true);

		assertThatCode(scheduler::refreshKeywords).doesNotThrowAnyException();
		verify(service).refreshIfNeeded(second);
		verify(targetRepository).findNextBatch(2L, BATCH_REQUEST);
	}

	@Test
	@DisplayName("100개 단위로 조회하고 마지막 ID 이후의 대상을 이어서 처리한다")
	void processesMultipleBatchesWithoutRepeatingTargets() {
		List<HospitalTreatment> firstBatch = LongStream.rangeClosed(1, 100).mapToObj(this::target).toList();
		HospitalTreatment last = target(101L);
		when(targetRepository.findNextBatch(0L, BATCH_REQUEST)).thenReturn(firstBatch);
		when(targetRepository.findNextBatch(100L, BATCH_REQUEST)).thenReturn(List.of(last));

		scheduler.refreshKeywords();

		verify(service, times(101)).refreshIfNeeded(any(HospitalTreatment.class));
		for (HospitalTreatment target : firstBatch) {
			verify(service).refreshIfNeeded(target);
		}
		verify(service).refreshIfNeeded(last);
		verify(targetRepository).findNextBatch(101L, BATCH_REQUEST);
	}

	@Test
	@DisplayName("활성화 설정이 없으면 스케줄 작업을 등록하지 않는다")
	void doesNotScheduleByDefault() {
		contextRunner().run(context -> {
			assertThat(context).doesNotHaveBean(ReviewKeywordScheduler.class);
			assertThat(context.getBean(ScheduledAnnotationBeanPostProcessor.class).getScheduledTasks()).isEmpty();
		});
		verifyNoInteractions(service, targetRepository);
	}

	@Test
	@DisplayName("명시적으로 비활성화하면 스케줄 작업을 등록하지 않는다")
	void disablesScheduledJob() {
		contextRunner().withPropertyValues("review-keyword.refresh.enabled=false").run(context ->
			assertThat(context).doesNotHaveBean(ReviewKeywordScheduler.class));
		verifyNoInteractions(service, targetRepository);
	}

	@Test
	@DisplayName("등록된 스케줄 작업이 조회 요청 없이 갱신 서비스를 호출한다")
	void runsScheduledJobWithoutLookupRequest() {
		HospitalTreatment target = target(1L);
		when(targetRepository.findNextBatch(0L, BATCH_REQUEST)).thenReturn(List.of(target));

		contextRunner().withPropertyValues("review-keyword.refresh.enabled=true").run(context -> {
			var tasks = context.getBean(ScheduledAnnotationBeanPostProcessor.class).getScheduledTasks();
			assertThat(tasks).hasSize(1);
			tasks.iterator().next().getTask().getRunnable().run();

			verify(service).refreshIfNeeded(target);
			verify(targetRepository).findNextBatch(1L, BATCH_REQUEST);
		});
	}

	@Test
	@DisplayName("활성화하면 최초 대기 1분과 실행 완료 후 대기 30분으로 등록한다")
	void registersScheduledJobWhenEnabled() {
		contextRunner().withPropertyValues("review-keyword.refresh.enabled=true").run(context -> {
			assertThat(context).hasSingleBean(ReviewKeywordScheduler.class);
			var tasks = context.getBean(ScheduledAnnotationBeanPostProcessor.class).getScheduledTasks();
			assertThat(tasks).hasSize(1);
			assertThat(tasks.iterator().next().getTask()).isInstanceOfSatisfying(FixedDelayTask.class, task -> {
				assertThat(task.getIntervalDuration()).isEqualTo(Duration.ofMinutes(30));
				assertThat(task.getInitialDelayDuration()).isEqualTo(Duration.ofMinutes(1));
			});
		});
		verifyNoInteractions(service, targetRepository);
	}

	private ApplicationContextRunner contextRunner() {
		return new ApplicationContextRunner().withUserConfiguration(SchedulingConfiguration.class)
			.withBean(ReviewKeywordTargetRepository.class, () -> targetRepository)
			.withBean(ReviewKeywordService.class, () -> service);
	}

	private HospitalTreatment target(long id) {
		HospitalTreatment target = HospitalTreatment.builder().build();
		ReflectionTestUtils.setField(target, "id", id);
		return target;
	}

	@Configuration(proxyBeanMethods = false)
	@EnableScheduling
	@Import(ReviewKeywordScheduler.class)
	static class SchedulingConfiguration {
	}
}
