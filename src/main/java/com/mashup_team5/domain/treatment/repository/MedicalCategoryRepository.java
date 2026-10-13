package com.mashup_team5.domain.treatment.repository;

import com.mashup_team5.domain.treatment.entity.MedicalCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalCategoryRepository extends JpaRepository<MedicalCategory, Long> {

	List<MedicalCategory> findAllByOrderByIdAsc();
}
