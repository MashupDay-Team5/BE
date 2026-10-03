package com.mashup_team5.domain.treatment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "medical_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicalCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "medical_category_id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Builder
	public MedicalCategory(String name) {
		this.name = name;
	}
}
