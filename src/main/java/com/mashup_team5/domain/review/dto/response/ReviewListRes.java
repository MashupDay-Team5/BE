package com.mashup_team5.domain.review.dto.response;

import com.mashup_team5.domain.review.entity.Review;
import com.mashup_team5.domain.review.enums.ReviewScope;
import com.mashup_team5.domain.treatment.entity.Treatment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public record ReviewListRes(
	Long hospitalId,
	Long treatmentId,
	ReviewScope scope,
	List<ReviewItemRes> items,
	long totalCount,
	int page,
	int size,
	boolean hasNext
) {

	public record ReviewItemRes(
		Long reviewId,
		String authorDisplayName,
		BigDecimal rating,
		@Schema(description = "생략하지 않은 전체 후기 본문") String content,
		List<ReceivedTreatmentRes> receivedTreatments,
		@Schema(description = "후기 전체 결제 금액. 알 수 없으면 null", nullable = true) Integer paidPrice,
		String doctorName,
		boolean willRevisit,
		boolean receiptVerified,
		boolean modoodocVisit,
		boolean reservationVisit,
		int helpfulCount,
		@Schema(description = "DB에 UTC 기준으로 저장된 작성 시각") OffsetDateTime createdAt
	) {

		public static ReviewItemRes from(Review review, List<ReceivedTreatmentRes> treatments) {
			String nickname = review.getUser() == null ? null : review.getUser().getNickname();
			String authorDisplayName = nickname == null || nickname.isBlank() ? "익명" : nickname;
			return new ReviewItemRes(
				review.getId(), authorDisplayName, review.getRating(), review.getContent(), treatments,
				review.getPaidPrice(), review.getDoctorName(), Boolean.TRUE.equals(review.getWillRevisit()),
				Boolean.TRUE.equals(review.getIsReceiptVerified()), Boolean.TRUE.equals(review.getIsModoodocVisit()),
				Boolean.TRUE.equals(review.getIsReservationVisit()),
				review.getHelpfulCount() == null ? 0 : review.getHelpfulCount(),
				review.getCreatedAt() == null ? null : review.getCreatedAt().atOffset(ZoneOffset.UTC)
			);
		}
	}

	public record ReceivedTreatmentRes(
		Long treatmentId,
		String name,
		@Schema(description = "현재 ERD에는 개별 시술 결제 금액이 없어 null 반환", nullable = true)
		Integer paidPrice
	) {

		public static ReceivedTreatmentRes from(Treatment treatment) {
			return new ReceivedTreatmentRes(treatment.getId(), treatment.getName(), null);
		}
	}
}
