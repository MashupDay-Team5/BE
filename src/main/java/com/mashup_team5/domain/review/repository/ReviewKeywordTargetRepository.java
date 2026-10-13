package com.mashup_team5.domain.review.repository;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewKeywordTargetRepository extends Repository<HospitalTreatment, Long> {

	@Transactional(readOnly = true)
	@Query("""
		select ht from HospitalTreatment ht
		join fetch ht.hospital
		join fetch ht.treatment
		where ht.hospital.id = :hospitalId and ht.treatment.id = :treatmentId
		""")
	Optional<HospitalTreatment> findByHospitalIdAndTreatmentId(@Param("hospitalId") Long hospitalId,
		@Param("treatmentId") Long treatmentId);

	@Transactional(readOnly = true)
	@Query("""
		select ht from HospitalTreatment ht
		join fetch ht.hospital
		join fetch ht.treatment
		where ht.id > :afterId
		order by ht.id
		""")
	List<HospitalTreatment> findNextBatch(@Param("afterId") Long afterId, Pageable pageable);
}
