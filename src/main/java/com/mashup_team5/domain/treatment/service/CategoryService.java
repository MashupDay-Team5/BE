package com.mashup_team5.domain.treatment.service;

import com.mashup_team5.domain.treatment.dto.CategoryResponse;
import com.mashup_team5.domain.treatment.dto.CategoryResponse.TreatmentCategoryResponse;
import com.mashup_team5.domain.treatment.dto.CategoryResponse.TreatmentResponse;
import com.mashup_team5.domain.treatment.entity.Treatment;
import com.mashup_team5.domain.treatment.entity.TreatmentCategory;
import com.mashup_team5.domain.treatment.repository.MedicalCategoryRepository;
import com.mashup_team5.domain.treatment.repository.TreatmentCategoryRepository;
import com.mashup_team5.domain.treatment.repository.TreatmentRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final MedicalCategoryRepository medicalCategoryRepository;
	private final TreatmentCategoryRepository treatmentCategoryRepository;
	private final TreatmentRepository treatmentRepository;

	public List<CategoryResponse> getCategories() {
		Map<Long, List<TreatmentResponse>> treatmentsByCategoryId = treatmentRepository.findAllWithTreatmentCategory()
			.stream()
			.collect(Collectors.groupingBy(
				treatment -> treatment.getTreatmentCategory().getId(),
				Collectors.mapping(this::toTreatmentResponse, Collectors.toList())
			));
		Map<Long, List<TreatmentCategoryResponse>> categoriesByMedicalCategoryId =
			treatmentCategoryRepository.findAllWithMedicalCategory()
				.stream()
				.collect(Collectors.groupingBy(
					treatmentCategory -> treatmentCategory.getMedicalCategory().getId(),
					Collectors.mapping(
						treatmentCategory -> toTreatmentCategoryResponse(treatmentCategory, treatmentsByCategoryId),
						Collectors.toList()
					)
				));

		return medicalCategoryRepository.findAllByOrderByIdAsc()
			.stream()
			.map(medicalCategory -> new CategoryResponse(
				medicalCategory.getId(),
				medicalCategory.getName(),
				categoriesByMedicalCategoryId.getOrDefault(medicalCategory.getId(), List.of())
			))
			.toList();
	}

	private TreatmentCategoryResponse toTreatmentCategoryResponse(
		TreatmentCategory treatmentCategory,
		Map<Long, List<TreatmentResponse>> treatmentsByCategoryId
	) {
		return new TreatmentCategoryResponse(
			treatmentCategory.getId(),
			treatmentCategory.getName(),
			treatmentsByCategoryId.getOrDefault(treatmentCategory.getId(), List.of())
		);
	}

	private TreatmentResponse toTreatmentResponse(Treatment treatment) {
		return new TreatmentResponse(treatment.getId(), treatment.getName());
	}
}
