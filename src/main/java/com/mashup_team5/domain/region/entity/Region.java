package com.mashup_team5.domain.region.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "region", uniqueConstraints = {
	@UniqueConstraint(name = "uk_region_parent_name", columnNames = {"parent_region_id", "name"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "region_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_region_id")
	private Region parentRegion;

	@Column(name = "name")
	private String name;

	@Column(name = "region_type")
	@Enumerated(EnumType.STRING)
	private RegionType regionType;

	@Builder
	public Region(Region parentRegion, String name, RegionType regionType) {
		this.parentRegion = parentRegion;
		this.name = name;
		this.regionType = regionType;
	}
}
