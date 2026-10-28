package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.hospital.repository.projection.HospitalTreatmentPriceRow;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HospitalTreatmentRepository extends JpaRepository<HospitalTreatment, Long> {

	boolean existsByHospital_IdAndTreatment_Id(Long hospitalId, Long treatmentId);

	@Query("""
		select new com.mashup_team5.domain.hospital.repository.projection.HospitalTreatmentPriceRow(
			ht.hospital.id, ht.id, t.id, t.name, ht.regularPrice
		)
		from HospitalTreatment ht
		join ht.treatment t
		left join t.treatmentCategory tc
		where ht.hospital.id in :hospitalIds
		  and (:medicalCategoryId is null or tc.medicalCategory.id = :medicalCategoryId)
		  and (:treatmentCategoryId is null or tc.id = :treatmentCategoryId)
		  and (:filterTreatments = false or t.id in :treatmentIds)
		order by ht.hospital.id asc, ht.id asc
		""")
	List<HospitalTreatmentPriceRow> findPriceRows(
		@Param("hospitalIds") Collection<Long> hospitalIds,
		@Param("medicalCategoryId") Long medicalCategoryId,
		@Param("treatmentCategoryId") Long treatmentCategoryId,
		@Param("treatmentIds") Collection<Long> treatmentIds,
		@Param("filterTreatments") boolean filterTreatments);
}
