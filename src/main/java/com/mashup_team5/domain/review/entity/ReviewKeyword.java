package com.mashup_team5.domain.review.entity;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Table(name = "review_keyword", uniqueConstraints = {
	@UniqueConstraint(name = "uk_review_keyword_hospital_treatment", columnNames = "hospital_treatment_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewKeyword {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_keyword_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hospital_treatment_id", nullable = false)
	private HospitalTreatment hospitalTreatment;

	@Size(max = 6)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "keywords", nullable = false) // 성공했을때만 저장, 키워드 없으면 빈 배열 저장
	private List<String> keywords;

	// 삭제된 후기도 기준으로 유지할 수 있도록 FK가 아닌 ID 값으로 저장한다.
	@Column(name = "last_analyzed_review_id", nullable = false)
	private Long lastAnalyzedReviewId;

	@Column(name = "created_at", nullable = false, columnDefinition = "DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Builder
	public ReviewKeyword(
		HospitalTreatment hospitalTreatment, List<String> keywords, Long lastAnalyzedReviewId,
		LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.hospitalTreatment = hospitalTreatment;
		this.keywords = keywords == null ? List.of() : List.copyOf(keywords);
		this.lastAnalyzedReviewId = lastAnalyzedReviewId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public void updateResult(List<String> keywords, Long lastAnalyzedReviewId, LocalDateTime generatedAt) {
		this.keywords = List.copyOf(keywords);
		this.lastAnalyzedReviewId = lastAnalyzedReviewId;
		this.updatedAt = generatedAt;
	}
}
