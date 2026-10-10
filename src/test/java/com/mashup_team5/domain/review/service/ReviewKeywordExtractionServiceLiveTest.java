package com.mashup_team5.domain.review.service;

import com.mashup_team5.global.config.OpenAiConfig;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

// 실제 호출 비용이 발생할 수 있으므로 수동 실행할 때만 OPENAI_LIVE_TEST=true로 설정한다.
@EnabledIfEnvironmentVariable(named = "OPENAI_LIVE_TEST", matches = "true")
class ReviewKeywordExtractionServiceLiveTest {

	@Test
	@DisplayName("실제 OpenAI API로 후기 키워드를 추출한다 — 수동 실행")
	void extractKeywordsWithRealReviews() {
		String apiKey = System.getenv("OPENAI_API_KEY");
		assertThat(apiKey)
			.as("테스트 실행 설정에 OPENAI_API_KEY를 지정해야 합니다.")
			.isNotBlank();

		String model = System.getenv("OPENAI_MODEL");
		if (model == null || model.isBlank()) {
			model = "gpt-4.1-mini";
		}
		ReviewKeywordExtractionService service = new ReviewKeywordExtractionService(
			new OpenAiConfig().openAiRestClient(Duration.ofSeconds(30)),
			JsonMapper.builder().build(), apiKey, model);

		// 같은 병원/시술의 가상 후기. 실제 후기로 바꿀 경우 개인정보를 먼저 제거한다.
		List<String> reviews = List.of(
			"설명을 자세하게 해주셨지만 대기가 길었어요.",
			"상담이 꼼꼼했고 직원들이 친절했어요.",
			"설명이 자세해서 좋았어요. 대기 시간은 길었습니다.",
			"직원들이 친절하고 상담도 꼼꼼했어요.",
			"대기가 길었지만 설명은 충분히 해주셨습니다.",
			"상담 과정에서 궁금한 점을 자세히 설명해주셨어요.",
			"직원분이 친절하게 안내해주셨고 상담도 꼼꼼했어요.",
			"예약했지만 대기 시간이 길어서 아쉬웠어요.",
			"질문에 자세히 답해주셔서 상담이 만족스러웠어요.",
			"직원들이 친절했지만 대기는 생각보다 길었어요.");

		List<String> keywords = service.extractKeywords(reviews);

		System.out.println("추출된 리뷰 키워드: " + keywords);
		assertThat(keywords)
			.hasSizeLessThanOrEqualTo(6)
			.doesNotHaveDuplicates()
			.allMatch(keyword -> !keyword.isBlank());
	}
}
