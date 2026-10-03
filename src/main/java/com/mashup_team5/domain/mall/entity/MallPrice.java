package com.mashup_team5.domain.mall.entity;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "mall_price", uniqueConstraints = {
	@UniqueConstraint(name = "uk_mall_price_mall_hospital_treatment", columnNames = {"mall_id", "hospital_treatment_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MallPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mall_price_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hospital_treatment_id")
	private HospitalTreatment hospitalTreatment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mall_id")
	private Mall mall;

	@Column(name = "price")
	private Integer price;

	@Builder
	public MallPrice(HospitalTreatment hospitalTreatment, Mall mall, Integer price) {
		this.hospitalTreatment = hospitalTreatment;
		this.mall = mall;
		this.price = price;
	}
}
