package com.mashup_team5.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewListReq {

	@Schema(description = "관련 후기 또는 같은 병원의 다른 후기", allowableValues = {"RELATED", "OTHER"}, defaultValue = "RELATED")
	private String scope = "RELATED";

	@Schema(description = "기본순은 최신순. 동률은 작성일과 후기 ID 내림차순",
		allowableValues = {"DEFAULT", "LATEST", "HIGHEST_RATING", "LOWEST_RATING", "MOST_HELPFUL"}, defaultValue = "DEFAULT")
	private String sort = "DEFAULT";

	@Schema(description = "0부터 시작하는 페이지", defaultValue = "0", minimum = "0")
	private Integer page = 0;

	@Schema(description = "페이지당 후기 수", defaultValue = "10", minimum = "1", maximum = "50")
	private Integer size = 10;
}
