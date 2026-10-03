package com.mashup_team5.domain.review.entity;

import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hospital_id")
	private Hospital hospital;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "paid_price")
	private Integer paidPrice;

	@Column(name = "rating", precision = 3, scale = 1)
	private BigDecimal rating;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "doctor_name")
	private String doctorName;

	@Column(name = "will_revisit")
	private Boolean willRevisit;

	@Column(name = "is_receipt_verified")
	private Boolean isReceiptVerified;

	@Column(name = "is_modoodoc_visit")
	private Boolean isModoodocVisit;

	@Column(name = "is_reservation_visit")
	private Boolean isReservationVisit;

	@Column(name = "helpful_count")
	private Integer helpfulCount;

	@Column(name = "created_at", columnDefinition = "DATETIME")
	private LocalDateTime createdAt;

	@Builder
	public Review(
		Hospital hospital, User user, Integer paidPrice, BigDecimal rating, String content,
		String doctorName, Boolean willRevisit, Boolean isReceiptVerified, Boolean isModoodocVisit,
		Boolean isReservationVisit, Integer helpfulCount, LocalDateTime createdAt) {
		this.hospital = hospital;
		this.user = user;
		this.paidPrice = paidPrice;
		this.rating = rating;
		this.content = content;
		this.doctorName = doctorName;
		this.willRevisit = willRevisit;
		this.isReceiptVerified = isReceiptVerified;
		this.isModoodocVisit = isModoodocVisit;
		this.isReservationVisit = isReservationVisit;
		this.helpfulCount = helpfulCount;
		this.createdAt = createdAt;
	}
}
