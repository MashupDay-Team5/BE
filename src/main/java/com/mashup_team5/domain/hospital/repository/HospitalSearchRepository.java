package com.mashup_team5.domain.hospital.repository;

public interface HospitalSearchRepository {

	HospitalIdSlice findHospitalIds(HospitalSearchCondition condition);

	long countHospitals(HospitalSearchCondition condition);
}
