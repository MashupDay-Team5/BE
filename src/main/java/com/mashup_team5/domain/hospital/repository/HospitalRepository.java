package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.entity.Hospital;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HospitalRepository extends JpaRepository<Hospital, Long>, HospitalSearchRepository {

	@Query("""
		select h from Hospital h
		join fetch h.region r
		left join fetch r.parentRegion parent
		left join fetch parent.parentRegion
		where h.id in :hospitalIds
		""")
	List<Hospital> findAllWithRegionByIdIn(@Param("hospitalIds") Collection<Long> hospitalIds);
}
