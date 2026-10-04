package com.mashup_team5.domain.review.entity;

import com.mashup_team5.domain.treatment.entity.Treatment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "review_treatment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewTreatment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_treatment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "treatment_id")
	private Treatment treatment;

	@Builder
	public ReviewTreatment(Review review, Treatment treatment) {
		this.review = review;
		this.treatment = treatment;
	}
}
