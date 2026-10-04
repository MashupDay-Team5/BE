package com.mashup_team5.domain.hospital.entity;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "hospital_treatment", uniqueConstraints = {
	@UniqueConstraint(name = "uk_hospital_treatment_hospital_treatment", columnNames = {"hospital_id", "treatment_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HospitalTreatment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hospital_treatment_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hospital_id")
	private Hospital hospital;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "treatment_id")
	private Treatment treatment;

	@Column(name = "regular_price")
	private Integer regularPrice;

	@Column(name = "is_special_offer")
	private Boolean isSpecialOffer;

	@Builder
	public HospitalTreatment(
		Hospital hospital, Treatment treatment, Integer regularPrice, Boolean isSpecialOffer) {
		this.hospital = hospital;
		this.treatment = treatment;
		this.regularPrice = regularPrice;
		this.isSpecialOffer = isSpecialOffer;
	}
}
