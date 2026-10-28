package com.mashup_team5.domain.region.repository;

import com.mashup_team5.domain.region.entity.Region;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

	long countByIdIn(Collection<Long> ids);

	List<Region> findAllByParentRegionIsNullOrderByIdAsc();

	List<Region> findAllByParentRegionIdOrderByIdAsc(Long parentId);
}
