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
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private static final Set<Long> HIDDEN_FILTER_TREATMENT_IDS = Set.of(578L, 579L, 580L, 582L, 583L, 584L, 585L, 587L);

	private final MedicalCategoryRepository medicalCategoryRepository;
	private final TreatmentCategoryRepository treatmentCategoryRepository;
	private final TreatmentRepository treatmentRepository;

	public List<CategoryResponse> getCategories() {
		List<Treatment> treatments = treatmentRepository.findAllWithTreatmentCategory();
		Set<Long> categoriesWithTreatments = treatments.stream()
			.map(treatment -> treatment.getTreatmentCategory().getId())
			.collect(Collectors.toSet());
		Map<Long, List<TreatmentResponse>> treatmentsByCategoryId = treatments
			.stream()
			.filter(treatment -> !HIDDEN_FILTER_TREATMENT_IDS.contains(treatment.getId()))
			.collect(Collectors.groupingBy(
				treatment -> treatment.getTreatmentCategory().getId(),
				Collectors.mapping(this::toTreatmentResponse, Collectors.toList())
			));
		Map<Long, List<TreatmentCategoryResponse>> categoriesByMedicalCategoryId =
			treatmentCategoryRepository.findAllWithMedicalCategory()
				.stream()
				.filter(category -> !categoriesWithTreatments.contains(category.getId())
					|| treatmentsByCategoryId.containsKey(category.getId()))
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
