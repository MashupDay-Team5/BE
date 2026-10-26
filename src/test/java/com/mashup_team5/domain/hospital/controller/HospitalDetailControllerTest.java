package com.mashup_team5.domain.hospital.controller;

import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.mall.entity.Mall;
import com.mashup_team5.domain.mall.entity.MallPrice;
import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.region.entity.RegionType;
import com.mashup_team5.domain.review.entity.Review;
import com.mashup_team5.domain.review.entity.ReviewTreatment;
import com.mashup_team5.domain.treatment.entity.MedicalCategory;
import com.mashup_team5.domain.treatment.entity.Treatment;
import com.mashup_team5.domain.treatment.entity.TreatmentCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
	"spring.jpa.properties.hibernate.generate_statistics=true",
	"spring.datasource.url=jdbc:h2:mem:hospital-detail-test;MODE=MySQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER"
})
@AutoConfigureMockMvc
@Transactional
class HospitalDetailControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private Region seoul;
	private Region seocho;
	private Region seochoDong;
	private Hospital hospital;
	private Hospital noReviewHospital;
	private Treatment selectedTreatment;
	private Treatment otherTreatment;
	private Treatment unsupportedTreatment;
	private Mall discountMall;
	private Mall noPriceMall;

	@BeforeEach
	void setUp() {
		seoul = persist(Region.builder().name("서울특별시").regionType(RegionType.SIDO).build());
		seocho = persist(Region.builder()
				.parentRegion(seoul).name("서초구").regionType(RegionType.SIGUNGU).build());
		seochoDong = persist(Region.builder()
				.parentRegion(seocho).name("서초동").regionType(RegionType.DONG).build());
		MedicalCategory medicalCategory = persist(MedicalCategory.builder().name("안과").build());
		TreatmentCategory treatmentCategory = persist(TreatmentCategory.builder()
				.medicalCategory(medicalCategory).name("시력교정술").build());
		selectedTreatment = persist(Treatment.builder()
				.treatmentCategory(treatmentCategory).name("스마일라식").displayOrder(1).build());
		otherTreatment = persist(Treatment.builder()
				.treatmentCategory(treatmentCategory).name("라식").displayOrder(2).build());
		unsupportedTreatment = persist(Treatment.builder()
				.treatmentCategory(treatmentCategory).name("라섹").displayOrder(3).build());
		hospital = persist(hospital("OO안과"));
		noReviewHospital = persist(hospital("리뷰없는안과"));
		HospitalTreatment selectedHospitalTreatment = persist(hospitalTreatment(hospital, selectedTreatment, 1900000));
		persist(hospitalTreatment(hospital, otherTreatment, 1700000));
		persist(hospitalTreatment(noReviewHospital, selectedTreatment, 1800000));
		discountMall = persist(Mall.builder().name("할인몰").build());
		noPriceMall = persist(Mall.builder().name("가격없는몰").build());
		persist(MallPrice.builder()
				.hospitalTreatment(selectedHospitalTreatment).mall(discountMall).price(1500000).build());

		String[] ratings = {"5.0", "5.0", "5.0", "5.0", "5.0", "4.5", "4.5", "4.5", "4.5", "4.5"};
		for (int index = 0; index < ratings.length; index++) {
			Review review = persist(review(hospital, ratings[index]));
			if (index < 4) {
				persist(ReviewTreatment.builder().review(review).treatment(selectedTreatment).build());
			}
			if (index == 0 || index == 4) {
				persist(ReviewTreatment.builder().review(review).treatment(otherTreatment).build());
			}
		}
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("병원·시술 상세와 지역, 전체 평점 및 리뷰 분류를 반환한다")
	void getHospitalDetail() throws Exception {
		Statistics statistics = statistics();
		statistics.clear();

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), selectedTreatment.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.hospitalId").value(hospital.getId()))
				.andExpect(jsonPath("$.data.hospitalName").value("OO안과"))
				.andExpect(jsonPath("$.data.region.sidoId").value(seoul.getId()))
				.andExpect(jsonPath("$.data.region.sidoName").value("서울특별시"))
				.andExpect(jsonPath("$.data.region.sigunguId").value(seocho.getId()))
				.andExpect(jsonPath("$.data.region.sigunguName").value("서초구"))
				.andExpect(jsonPath("$.data.region.dongId").value(seochoDong.getId()))
				.andExpect(jsonPath("$.data.region.dongName").value("서초동"))
				.andExpect(jsonPath("$.data.rating").value(4.8))
				.andExpect(jsonPath("$.data.totalReviewCount").value(10))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(4))
				.andExpect(jsonPath("$.data.otherReviewCount").value(6))
				.andExpect(jsonPath("$.data.treatment.treatmentId").value(selectedTreatment.getId()))
				.andExpect(jsonPath("$.data.treatment.treatmentName").value("스마일라식"))
				.andExpect(jsonPath("$.data.treatment.regularPrice").value(1900000))
				.andExpect(jsonPath("$.data.treatment.discountPrice").doesNotExist())
				.andExpect(jsonPath("$.data.treatment.displayPrice").doesNotExist())
				.andExpect(jsonPath("$.data.treatment.priceType").doesNotExist())
				.andExpect(jsonPath("$.data.treatment.isSpecialOffer").doesNotExist());

		assertThat(statistics.getPrepareStatementCount()).isEqualTo(4);
	}

	@Test
	@DisplayName("리뷰가 없으면 평점과 모든 리뷰 수를 0으로 반환한다")
	void getHospitalDetailWithoutReviews() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					noReviewHospital.getId(), selectedTreatment.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.rating").value(0.0))
				.andExpect(jsonPath("$.data.totalReviewCount").value(0))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(0))
				.andExpect(jsonPath("$.data.otherReviewCount").value(0));
	}

	@Test
	@DisplayName("몰 가격이 있으면 할인 가격을 반환하고 없으면 null을 반환한다")
	void getHospitalDetailWithMallPrice() throws Exception {
		Statistics statistics = statistics();
		statistics.clear();

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), selectedTreatment.getId())
					.param("mallId", discountMall.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.treatment.regularPrice").value(1900000))
				.andExpect(jsonPath("$.data.treatment.discountPrice").value(1500000));

		assertThat(statistics.getPrepareStatementCount()).isEqualTo(5);

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), selectedTreatment.getId())
					.param("mallId", noPriceMall.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.treatment.regularPrice").value(1900000))
				.andExpect(jsonPath("$.data.treatment.discountPrice").doesNotExist());
	}

	@Test
	@DisplayName("존재하지 않는 병원은 Not Found로 처리한다")
	void rejectUnknownHospital() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					999999L, selectedTreatment.getId()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("COMMON404"));
	}

	@Test
	@DisplayName("병원이 제공하지 않는 시술은 병원·시술 Not Found로 처리한다")
	void rejectUnsupportedTreatment() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), unsupportedTreatment.getId()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("HOSPITAL_TREATMENT_NOT_FOUND404"));
	}

	@Test
	@DisplayName("1 미만의 병원·시술 ID는 Bad Request로 처리한다")
	void rejectInvalidIds() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					0L, selectedTreatment.getId()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_ID400"));

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), 0L))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_ID400"));
	}

	private Hospital hospital(String name) {
		return Hospital.builder()
				.region(seochoDong)
				.name(name)
				.hasSpecialist(true)
				.hasPublicPrice(true)
				.hasNightClinic(false)
				.hasHolidayClinic(false)
				.isReservable(true)
				.monthlyVisitCount(100)
				.build();
	}

	private HospitalTreatment hospitalTreatment(Hospital targetHospital, Treatment treatment, int regularPrice) {
		return HospitalTreatment.builder()
				.hospital(targetHospital)
				.treatment(treatment)
				.regularPrice(regularPrice)
				.isSpecialOffer(false)
				.build();
	}

	private Review review(Hospital targetHospital, String rating) {
		return Review.builder()
				.hospital(targetHospital)
				.rating(new BigDecimal(rating))
				.build();
	}

	private <T> T persist(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	private Statistics statistics() {
		return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
	}
}
