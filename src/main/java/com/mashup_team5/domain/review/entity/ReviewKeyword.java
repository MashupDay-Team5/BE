package com.mashup_team5.domain.review.entity;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "review_keyword", uniqueConstraints = {
	@UniqueConstraint(name = "uk_review_keyword_hospital_treatment_keyword", columnNames = {"hospital_treatment_id", "keyword"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewKeyword {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_keyword_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hospital_treatment_id")
	private HospitalTreatment hospitalTreatment;

	@Column(name = "keyword")
	private String keyword;

	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "DATETIME")
	private LocalDateTime updatedAt;

	@Builder
	public ReviewKeyword(
		HospitalTreatment hospitalTreatment, String keyword, LocalDateTime createdAt,
		LocalDateTime updatedAt) {
		this.hospitalTreatment = hospitalTreatment;
		this.keyword = keyword;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
