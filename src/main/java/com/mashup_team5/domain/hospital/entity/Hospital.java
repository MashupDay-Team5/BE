package com.mashup_team5.domain.hospital.entity;

import com.mashup_team5.domain.region.entity.Region;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "hospital")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hospital {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hospital_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	private Region region;

	@Column(name = "name")
	private String name;

	@Column(name = "latitude", precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(name = "longitude", precision = 10, scale = 7)
	private BigDecimal longitude;

	@Column(name = "has_specialist")
	private Boolean hasSpecialist;

	@Column(name = "has_public_price")
	private Boolean hasPublicPrice;

	@Column(name = "has_night_clinic")
	private Boolean hasNightClinic;

	@Column(name = "has_holiday_clinic")
	private Boolean hasHolidayClinic;

	@Column(name = "is_reservable")
	private Boolean isReservable;

	@Builder
	public Hospital(
		Region region, String name, BigDecimal latitude, BigDecimal longitude, Boolean hasSpecialist,
		Boolean hasPublicPrice, Boolean hasNightClinic, Boolean hasHolidayClinic, Boolean isReservable) {
		this.region = region;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.hasSpecialist = hasSpecialist;
		this.hasPublicPrice = hasPublicPrice;
		this.hasNightClinic = hasNightClinic;
		this.hasHolidayClinic = hasHolidayClinic;
		this.isReservable = isReservable;
	}
}
