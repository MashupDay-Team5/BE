package com.mashup_team5.domain.region.dto;

import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.region.entity.RegionType;

public record RegionResponse(Long regionId, String name, RegionType regionType) {

	public static RegionResponse from(Region region) {
		return new RegionResponse(region.getId(), region.getName(), region.getRegionType());
	}
}
