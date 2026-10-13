package com.mashup_team5.domain.review.service;

import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ReviewKeywordExtractionServiceTest {

	private static final List<String> REVIEW_CONTENTS = List.of(
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

	private final JsonMapper jsonMapper = JsonMapper.builder().build();
	private MockRestServiceServer server;
	private RestClient restClient;
	private ReviewKeywordExtractionService service;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder().baseUrl("https://api.openai.com/v1");
		server = MockRestServiceServer.bindTo(builder).build();
		restClient = builder.build();
		service = new ReviewKeywordExtractionService(restClient, jsonMapper, "test-api-key", "gpt-4.1-mini");
	}

	@AfterEach
	void verifyRequests() {
		server.verify();
	}

	@Test
	@DisplayName("구조화된 응답 형식으로 요청하고 키워드를 반환한다")
	void extractsKeywordsWithStructuredOutput() {
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andExpect(method(HttpMethod.POST))
			.andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-api-key"))
			.andExpect(request -> {
				JsonNode body = jsonMapper.readTree(((MockClientHttpRequest)request).getBodyAsString());
				assertThat(body.path("model").stringValue()).isEqualTo("gpt-4.1-mini");
				assertThat(body.path("store").booleanValue()).isFalse();
				assertThat(body.path("max_output_tokens").intValue()).isEqualTo(512);
				assertThat(body.at("/text/format/type").stringValue()).isEqualTo("json_schema");
				assertThat(body.at("/text/format/strict").booleanValue()).isTrue();
				assertThat(body.at("/text/format/schema/properties/keywords/maxItems").intValue()).isEqualTo(6);
				assertThat(jsonMapper.readTree(body.path("input").stringValue()).path("reviews"))
					.isEqualTo(jsonMapper.valueToTree(REVIEW_CONTENTS));
			})
			.andRespond(withSuccess(responseWithKeywords(List.of("친절해요")), MediaType.APPLICATION_JSON));

		assertThat(service.extractKeywords(REVIEW_CONTENTS))
			.containsExactly("친절해요");
	}

	@Test
	@DisplayName("빈 본문은 제외하고 이메일과 전화번호를 마스킹한다")
	void filtersEmptyReviewsAndMasksContactInformation() {
		List<String> reviews = new ArrayList<>(REVIEW_CONTENTS);
		reviews.addAll(Arrays.asList(null, "", "  ",
			" 문의 test@example.com, 010-1234-5678. 설명이 친절했어요. "));
		List<String> expectedReviews = new ArrayList<>(REVIEW_CONTENTS);
		expectedReviews.add("문의 [개인정보], [개인정보]. 설명이 친절했어요.");

		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andExpect(request -> {
				JsonNode body = jsonMapper.readTree(((MockClientHttpRequest)request).getBodyAsString());
				assertThat(jsonMapper.readTree(body.path("input").stringValue()).path("reviews"))
					.isEqualTo(jsonMapper.valueToTree(expectedReviews));
			})
			.andRespond(withSuccess(responseWithKeywords(List.of()), MediaType.APPLICATION_JSON));

		assertThat(service.extractKeywords(reviews)).isEmpty();
	}

	@Test
	@DisplayName("리뷰가 없으면 OpenAI 요청 없이 빈 목록을 반환한다")
	void skipsRequestWithoutReviewContents() {
		assertThat(service.extractKeywords(null)).isEmpty();
		assertThat(service.extractKeywords(List.of())).isEmpty();
		assertThat(service.extractKeywords(Arrays.asList(null, "", " \n "))).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 5, 9})
	@DisplayName("리뷰가 10개 미만이면 OpenAI 요청 없이 빈 목록을 반환한다")
	void skipsRequestBelowMinimumReviewCount(int count) {
		assertThat(service.extractKeywords(REVIEW_CONTENTS.subList(0, count))).isEmpty();
	}

	@Test
	@DisplayName("null과 공백 본문은 최소 리뷰 개수에 포함하지 않는다")
	void excludesEmptyContentsFromMinimumReviewCount() {
		List<String> reviews = new ArrayList<>(REVIEW_CONTENTS.subList(0, 9));
		reviews.addAll(Arrays.asList(null, "", "  "));

		assertThat(service.extractKeywords(reviews)).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(ints = {10, 11})
	@DisplayName("리뷰가 10개 이상이면 OpenAI 요청을 보낸다")
	void requestsWithMinimumReviewCountOrMore(int count) {
		List<String> reviews = new ArrayList<>(REVIEW_CONTENTS);
		if (count > reviews.size()) {
			reviews.add("추가 후기에서도 설명이 자세했다고 느꼈어요.");
		}
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withSuccess(responseWithKeywords(List.of("자세한 설명")), MediaType.APPLICATION_JSON));

		assertThat(service.extractKeywords(reviews)).containsExactly("자세한 설명");
	}

	@Test
	@DisplayName("키워드의 공백과 중복을 제거하고 최대 6개를 반환한다")
	void normalizesKeywordsAndLimitsCount() {
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withSuccess(responseWithKeywords(List.of(
				" 친절해요 ", "친절해요", "", " ", "설명이 자세해요", "대기가 길어요",
				"시설이 깨끗해요", "가격이 합리적이에요", "예약이 편해요", "접근성이 좋아요")),
				MediaType.APPLICATION_JSON));

		assertThat(service.extractKeywords(REVIEW_CONTENTS)).containsExactly(
			"친절해요", "설명이 자세해요", "대기가 길어요", "시설이 깨끗해요", "가격이 합리적이에요", "예약이 편해요");
	}

	@Test
	@DisplayName("추출된 키워드가 없으면 빈 목록을 반환한다")
	void returnsEmptyKeywords() {
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withSuccess(responseWithKeywords(List.of()), MediaType.APPLICATION_JSON));

		assertThat(service.extractKeywords(REVIEW_CONTENTS)).isEmpty();
	}

	@Test
	@DisplayName("API Key가 없으면 외부 요청을 보내지 않고 설정 오류를 반환한다")
	void rejectsMissingApiKey() {
		service = new ReviewKeywordExtractionService(restClient, jsonMapper, " ", "gpt-4.1-mini");

		assertError(ErrorCode.OPENAI_NOT_CONFIGURED);
	}

	@Test
	@DisplayName("모델이 없으면 외부 요청을 보내지 않고 설정 오류를 반환한다")
	void rejectsMissingModel() {
		service = new ReviewKeywordExtractionService(restClient, jsonMapper, "test-api-key", "");

		assertError(ErrorCode.OPENAI_NOT_CONFIGURED);
	}

	@ParameterizedTest
	@ValueSource(ints = {400, 401, 429, 500, 503})
	@DisplayName("외부 API 오류를 공통 요청 실패로 처리한다")
	void handlesOpenAiErrors(int status) {
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withStatus(HttpStatus.valueOf(status)));

		assertError(ErrorCode.OPENAI_REQUEST_FAILED);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@DisplayName("네트워크 오류와 타임아웃을 요청 실패로 처리한다")
	void handlesNetworkErrors(boolean timeout) {
		IOException exception = timeout ? new SocketTimeoutException("timeout") : new IOException("unavailable");
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withException(exception));

		assertError(ErrorCode.OPENAI_REQUEST_FAILED);
	}

	@ParameterizedTest
	@ValueSource(strings = {"incomplete", "failed", "queued", "in_progress", "cancelled"})
	@DisplayName("완료되지 않은 응답은 키워드로 사용하지 않는다")
	void rejectsUncompletedResponse(String status) {
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withSuccess(jsonMapper.writeValueAsString(Map.of("status", status, "output", List.of())),
				MediaType.APPLICATION_JSON));

		assertError(ErrorCode.OPENAI_INVALID_RESPONSE);
	}

	@ParameterizedTest
	@ValueSource(strings = {"{}", "{\"keywords\":null}", "{\"keywords\":\"친절해요\"}",
		"{\"keywords\":[1]}", "{\"keywords\":[null]}", "{\"keywords\":[true]}", "not-json"})
	@DisplayName("키워드 응답 형식이 올바르지 않으면 오류를 반환한다")
	void rejectsInvalidKeywordOutput(String outputText) {
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withSuccess(responseWithText(outputText), MediaType.APPLICATION_JSON));

		assertError(ErrorCode.OPENAI_INVALID_RESPONSE);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"{\"status\":\"completed\",\"output\":[]}",
		"{\"status\":\"completed\",\"output\":null}",
		"{\"status\":\"completed\",\"output\":[{\"type\":\"message\",\"content\":null}]}",
		"{\"status\":\"completed\",\"output\":[{\"type\":\"message\",\"content\":[{\"type\":\"refusal\",\"refusal\":\"거절\"}]}]}",
		"{\"status\":\"completed\",\"output\":[{\"type\":\"message\",\"content\":[{\"type\":\"output_text\",\"text\":null}]}]}"
	})
	@DisplayName("출력 누락이나 거절 응답은 성공한 빈 키워드와 구분한다")
	void rejectsMissingOutputOrRefusal(String response) {
		server.expect(requestTo("https://api.openai.com/v1/responses"))
			.andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

		assertError(ErrorCode.OPENAI_INVALID_RESPONSE);
	}

	private String responseWithKeywords(List<String> keywords) {
		return responseWithText(jsonMapper.writeValueAsString(Map.of("keywords", keywords)));
	}

	private String responseWithText(String text) {
		return jsonMapper.writeValueAsString(Map.of(
			"status", "completed",
			"output", List.of(Map.of(
				"type", "message", "content", List.of(Map.of("type", "output_text", "text", text))))));
	}

	private void assertError(ErrorCode errorCode) {
		assertThatThrownBy(() -> service.extractKeywords(REVIEW_CONTENTS))
			.isInstanceOfSatisfying(CustomException.class,
				exception -> assertThat(exception.getErrorCode()).isEqualTo(errorCode));
	}
}
