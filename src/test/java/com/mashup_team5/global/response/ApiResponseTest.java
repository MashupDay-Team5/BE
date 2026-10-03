package com.mashup_team5.global.response;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

	private final JsonMapper jsonMapper = JsonMapper.builder().build();

	@Test
	@DisplayName("성공 응답은 공통 코드와 메시지, 전달한 데이터를 가진다")
	void createsSuccessResponse() {
		Map<String, String> data = Map.of("name", "예시안과");

		ApiResponse<Map<String, String>> response = ApiResponse.onSuccess(data);

		assertThat(response.isSuccess()).isTrue();
		assertThat(response.getCode()).isEqualTo("COMMON200");
		assertThat(response.getMessage()).isEqualTo("성공입니다.");
		assertThat(response.getData()).isEqualTo(data);
	}

	@Test
	@DisplayName("실패 응답은 전달한 오류 코드와 메시지, 데이터를 가진다")
	void createsFailureResponse() {
		Map<String, String> data = Map.of("field", "minPrice");

		ApiResponse<Map<String, String>> response = ApiResponse.onFailure(
			"INVALID_PRICE_RANGE", "최소 예산은 최대 예산을 초과할 수 없습니다.", data);

		assertThat(response.isSuccess()).isFalse();
		assertThat(response.getCode()).isEqualTo("INVALID_PRICE_RANGE");
		assertThat(response.getMessage()).isEqualTo("최소 예산은 최대 예산을 초과할 수 없습니다.");
		assertThat(response.getData()).isEqualTo(data);
	}

	@Test
	@DisplayName("성공 응답은 success, code, message, data 네 필드로 직렬화된다")
	void serializesSuccessResponse() {
		ApiResponse<Map<String, String>> response = ApiResponse.onSuccess(Map.of("name", "예시안과"));

		String json = jsonMapper.writeValueAsString(response);

		assertThat(jsonMapper.readTree(json)).isEqualTo(jsonMapper.readTree("""
			{
			  "success": true,
			  "code": "COMMON200",
			  "message": "성공입니다.",
			  "data": {"name": "예시안과"}
			}
			"""));
	}

	@Test
	@DisplayName("성공 응답에 데이터가 없어도 data 필드를 null로 유지한다")
	void serializesSuccessResponseWithNullData() {
		ApiResponse<Void> response = ApiResponse.onSuccess(null);

		String json = jsonMapper.writeValueAsString(response);

		assertThat(jsonMapper.readTree(json)).isEqualTo(jsonMapper.readTree("""
			{
			  "success": true,
			  "code": "COMMON200",
			  "message": "성공입니다.",
			  "data": null
			}
			"""));
	}

	@Test
	@DisplayName("실패 응답에 데이터가 없어도 data 필드를 null로 유지한다")
	void serializesFailureResponseWithNullData() {
		ApiResponse<Void> response = ApiResponse.onFailure(
			"INVALID_PRICE_RANGE", "최소 예산은 최대 예산을 초과할 수 없습니다.", null);

		String json = jsonMapper.writeValueAsString(response);

		assertThat(jsonMapper.readTree(json)).isEqualTo(jsonMapper.readTree("""
			{
			  "success": false,
			  "code": "INVALID_PRICE_RANGE",
			  "message": "최소 예산은 최대 예산을 초과할 수 없습니다.",
			  "data": null
			}
			"""));
	}

	@Test
	@DisplayName("실패 응답에 추가 데이터가 있으면 함께 직렬화한다")
	void serializesFailureResponseWithData() {
		ApiResponse<Map<String, String>> response = ApiResponse.onFailure(
			"INVALID_FILTER", "잘못된 필터입니다.", Map.of("field", "regionIds"));

		String json = jsonMapper.writeValueAsString(response);

		assertThat(jsonMapper.readTree(json)).isEqualTo(jsonMapper.readTree("""
			{
			  "success": false,
			  "code": "INVALID_FILTER",
			  "message": "잘못된 필터입니다.",
			  "data": {"field": "regionIds"}
			}
			"""));
	}

	@Test
	@DisplayName("빈 목록은 null이 아닌 빈 배열로 직렬화한다")
	void serializesEmptyListAsArray() {
		ApiResponse<List<String>> response = ApiResponse.onSuccess(List.of());

		String json = jsonMapper.writeValueAsString(response);

		assertThat(jsonMapper.readTree(json)).isEqualTo(jsonMapper.readTree("""
			{
			  "success": true,
			  "code": "COMMON200",
			  "message": "성공입니다.",
			  "data": []
			}
			"""));
	}
}
