package com.mashup_team5.domain.treatment.controller;

import com.mashup_team5.domain.treatment.entity.MedicalCategory;
import com.mashup_team5.domain.treatment.entity.Treatment;
import com.mashup_team5.domain.treatment.entity.TreatmentCategory;
import com.mashup_team5.domain.treatment.repository.MedicalCategoryRepository;
import com.mashup_team5.domain.treatment.repository.TreatmentCategoryRepository;
import com.mashup_team5.domain.treatment.repository.TreatmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MedicalCategoryRepository medicalCategoryRepository;

	@Autowired
	private TreatmentCategoryRepository treatmentCategoryRepository;

	@Autowired
	private TreatmentRepository treatmentRepository;

	@Test
	@DisplayName("진료 분류 전체 계층을 시술 노출순서 오름차순으로 반환한다")
	void getCategories() throws Exception {
		MedicalCategory medicalCategory = medicalCategoryRepository.save(MedicalCategory.builder()
			.name("안과")
			.build());
		TreatmentCategory treatmentCategory = treatmentCategoryRepository.save(TreatmentCategory.builder()
			.medicalCategory(medicalCategory)
			.name("시력교정술")
			.build());
		Treatment smileLasik = treatmentRepository.save(Treatment.builder()
			.treatmentCategory(treatmentCategory)
			.name("스마일라식")
			.displayOrder(2)
			.build());
		Treatment lasik = treatmentRepository.save(Treatment.builder()
			.treatmentCategory(treatmentCategory)
			.name("라식")
			.displayOrder(1)
			.build());

		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.length()").value(1))
			.andExpect(jsonPath("$.data[0].medicalCategoryId").value(medicalCategory.getId()))
			.andExpect(jsonPath("$.data[0].name").value("안과"))
			.andExpect(jsonPath("$.data[0].treatmentCategories.length()").value(1))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatmentCategoryId")
				.value(treatmentCategory.getId()))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].name").value("시력교정술"))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments.length()").value(2))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments[0].treatmentId")
				.value(lasik.getId()))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments[0].name").value("라식"))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments[1].treatmentId")
				.value(smileLasik.getId()))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments[1].name").value("스마일라식"));
	}

	@Test
	@DisplayName("진료 분류 데이터가 없으면 빈 배열을 반환한다")
	void getEmptyCategories() throws Exception {
		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data").isEmpty());
	}
}
