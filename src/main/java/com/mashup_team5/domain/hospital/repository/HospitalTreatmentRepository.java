package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalTreatmentRepository extends JpaRepository<HospitalTreatment, Long> {

	boolean existsByHospital_IdAndTreatment_Id(Long hospitalId, Long treatmentId);
}
