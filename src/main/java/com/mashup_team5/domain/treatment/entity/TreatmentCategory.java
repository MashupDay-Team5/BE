package com.mashup_team5.domain.treatment.entity;

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
@Table(name = "treatment_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TreatmentCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "treatment_category_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medical_category_id")
	private MedicalCategory medicalCategory;

	@Column(name = "name")
	private String name;

	@Builder
	public TreatmentCategory(MedicalCategory medicalCategory, String name) {
		this.medicalCategory = medicalCategory;
		this.name = name;
	}
}
