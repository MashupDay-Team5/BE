package com.mashup_team5.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ReviewKeywordRes(
	@Schema(description = "병원 ID", example = "1") Long hospitalId,
	@Schema(description = "시술 ID", example = "2") Long treatmentId,
	@ArraySchema(maxItems = 6, uniqueItems = true,
		schema = @Schema(description = "저장된 순서의 후기 키워드", type = "string"))
	List<String> keywords
) {
}
