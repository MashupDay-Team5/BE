package com.mashup_team5.domain.region.service;

import com.mashup_team5.domain.region.dto.RegionResponse;
import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.region.repository.RegionRepository;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

	private final RegionRepository regionRepository;

	public List<RegionResponse> getRegions(Long parentId) {
		if (parentId == null) {
			return toResponses(regionRepository.findAllByParentRegionIsNullOrderByIdAsc());
		}

		List<Region> regions = regionRepository.findAllByParentRegionIdOrderByIdAsc(parentId);
		if (regions.isEmpty() && !regionRepository.existsById(parentId)) {
			throw new CustomException(ErrorCode.NOT_FOUND);
		}
		return toResponses(regions);
	}

	private List<RegionResponse> toResponses(List<Region> regions) {
		return regions.stream()
			.map(RegionResponse::from)
			.toList();
	}
}
