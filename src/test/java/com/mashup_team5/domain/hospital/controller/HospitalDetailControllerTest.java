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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
		hospital = persist(hospitalBuilder("OO안과").rating(new BigDecimal("4.2")).reviewCount(120)
				.address("서울특별시 서초구 서초대로 123").build());
		noReviewHospital = persist(hospitalBuilder("리뷰없는안과").build());
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
				.andExpect(jsonPath("$.data.address").value("서울특별시 서초구 서초대로 123"))
				.andExpect(jsonPath("$.data.region.sidoId").value(seoul.getId()))
				.andExpect(jsonPath("$.data.region.sidoName").value("서울특별시"))
				.andExpect(jsonPath("$.data.region.sigunguId").value(seocho.getId()))
				.andExpect(jsonPath("$.data.region.sigunguName").value("서초구"))
				.andExpect(jsonPath("$.data.region.dongId").value(seochoDong.getId()))
				.andExpect(jsonPath("$.data.region.dongName").value("서초동"))
				.andExpect(jsonPath("$.data.rating").value(4.2))
				.andExpect(jsonPath("$.data.totalReviewCount").value(120))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(4))
				.andExpect(jsonPath("$.data.otherReviewCount").value(116))
				.andExpect(jsonPath("$.data.treatment.treatmentId").value(selectedTreatment.getId()))
				.andExpect(jsonPath("$.data.treatment.treatmentName").value("스마일라식"))
				.andExpect(jsonPath("$.data.treatment.regularPrice").value(1900000))
				.andExpect(jsonPath("$.data.treatment.discountPrice").hasJsonPath())
				.andExpect(jsonPath("$.data.treatment.discountPrice").value(org.hamcrest.Matchers.nullValue()))
				.andExpect(jsonPath("$.data.treatment.displayPrice").doesNotExist())
				.andExpect(jsonPath("$.data.treatment.priceType").doesNotExist())
				.andExpect(jsonPath("$.data.treatment.isSpecialOffer").doesNotExist());

		assertThat(statistics.getPrepareStatementCount()).isEqualTo(3);
	}

	@Test
	@DisplayName("리뷰가 없으면 평점과 모든 리뷰 수를 0으로 반환한다")
	void getHospitalDetailWithoutReviews() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					noReviewHospital.getId(), selectedTreatment.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.rating").value(0.0))
				.andExpect(jsonPath("$.data.address").value(org.hamcrest.Matchers.nullValue()))
				.andExpect(jsonPath("$.data.totalReviewCount").value(0))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(0))
				.andExpect(jsonPath("$.data.otherReviewCount").value(0));
	}

	@Test
	@DisplayName("몰 가격이 있으면 할인 가격을 반환하고 없으면 상세 정보와 null 할인 가격을 반환한다")
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
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.code").value("COMMON200"))
				.andExpect(jsonPath("$.data.hospitalId").value(hospital.getId()))
				.andExpect(jsonPath("$.data.hospitalName").value("OO안과"))
				.andExpect(jsonPath("$.data.address").value("서울특별시 서초구 서초대로 123"))
				.andExpect(jsonPath("$.data.rating").value(4.2))
				.andExpect(jsonPath("$.data.totalReviewCount").value(120))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(4))
				.andExpect(jsonPath("$.data.otherReviewCount").value(116))
				.andExpect(jsonPath("$.data.treatment.treatmentId").value(selectedTreatment.getId()))
				.andExpect(jsonPath("$.data.treatment.regularPrice").value(1900000))
				.andExpect(jsonPath("$.data.treatment.discountPrice").hasJsonPath())
				.andExpect(jsonPath("$.data.treatment.discountPrice").value(org.hamcrest.Matchers.nullValue()));
	}

	@Test
	@DisplayName("존재하지 않는 병원은 Not Found로 처리한다")
	void rejectUnknownHospital() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					999999L, selectedTreatment.getId()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("HOSPITAL_TREATMENT_NOT_FOUND"));
	}

	@Test
	@DisplayName("병원이 제공하지 않는 시술은 병원·시술 Not Found로 처리한다")
	void rejectUnsupportedTreatment() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), unsupportedTreatment.getId()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value("HOSPITAL_TREATMENT_NOT_FOUND"));
	}

	@Test
	@DisplayName("1 미만의 병원·시술 ID는 Bad Request로 처리한다")
	void rejectInvalidIds() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					0L, selectedTreatment.getId()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_ID"));

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), 0L))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_ID"));
	}

	@Test
	@DisplayName("시술 관련 리뷰가 전체 리뷰 수보다 많아도 다른 리뷰 수는 음수가 되지 않는다")
	void clampOtherReviewCountToZero() throws Exception {
		entityManager.createQuery("update Hospital h set h.reviewCount = 2 where h.id = :hospitalId")
				.setParameter("hospitalId", hospital.getId()).executeUpdate();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), selectedTreatment.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.totalReviewCount").value(2))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(4))
				.andExpect(jsonPath("$.data.otherReviewCount").value(0));
	}

	@Test
	@DisplayName("저장된 통계가 null이어도 시술 관련 리뷰 수는 실제 데이터로 계산한다")
	void countRelatedReviewsWithNullStatistics() throws Exception {
		entityManager.createQuery("""
				update Hospital h set h.rating = null, h.reviewCount = null where h.id = :hospitalId
				""").setParameter("hospitalId", hospital.getId()).executeUpdate();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), selectedTreatment.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.rating").value(0.0))
				.andExpect(jsonPath("$.data.totalReviewCount").value(0))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(4))
				.andExpect(jsonPath("$.data.otherReviewCount").value(0));
	}

	@Test
	@DisplayName("여러 시술과 연결된 리뷰와 중복 연결은 선택한 시술의 리뷰 수에 한 번만 포함한다")
	void countRelatedReviewsBySelectedTreatment() throws Exception {
		Review review = entityManager.createQuery("select r from Review r where r.hospital.id = :hospitalId order by r.id", Review.class)
				.setParameter("hospitalId", hospital.getId()).setMaxResults(1).getSingleResult();
		persist(ReviewTreatment.builder().review(review).treatment(otherTreatment).build());
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					hospital.getId(), otherTreatment.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.totalReviewCount").value(120))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(2))
				.andExpect(jsonPath("$.data.otherReviewCount").value(118));
	}

	@Test
	@DisplayName("실제 리뷰가 없어도 병원에 저장된 통계를 반환한다")
	void getStoredStatisticsWithoutReviewRows() throws Exception {
		entityManager.createQuery("""
				update Hospital h set h.rating = 4.6, h.reviewCount = 50 where h.id = :hospitalId
				""").setParameter("hospitalId", noReviewHospital.getId()).executeUpdate();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}",
					noReviewHospital.getId(), selectedTreatment.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.rating").value(4.6))
				.andExpect(jsonPath("$.data.totalReviewCount").value(50))
				.andExpect(jsonPath("$.data.relatedReviewCount").value(0))
				.andExpect(jsonPath("$.data.otherReviewCount").value(50));
	}

	private Hospital.HospitalBuilder hospitalBuilder(String name) {
		return Hospital.builder()
				.region(seochoDong)
				.name(name)
				.hasSpecialist(true)
				.hasPublicPrice(true)
				.hasNightClinic(false)
				.hasHolidayClinic(false)
				.isReservable(true)
				.monthlyVisitCount(100);
	}

	@Test
	@DisplayName("상세에서 존재하지 않는 몰은 MALL_NOT_FOUND를 반환한다")
	void rejectUnknownMall() throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}", hospital.getId(), selectedTreatment.getId())
						.param("mallId", "999999"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("MALL_NOT_FOUND"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"abc", "-1", "0", "9223372036854775808"})
	@DisplayName("상세 경로 ID의 바인딩 및 범위 오류는 INVALID_ID를 반환한다")
	void rejectMalformedPathIds(String value) throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}", value, selectedTreatment.getId()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_ID"));
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}", hospital.getId(), value))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_ID"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"abc", "-1", "0", ""})
	@DisplayName("상세의 잘못된 몰 ID는 INVALID_ID를 반환한다")
	void rejectMalformedMallIds(String value) throws Exception {
		mockMvc.perform(get("/api/hospitals/{hospitalId}/treatments/{treatmentId}", hospital.getId(), selectedTreatment.getId())
						.param("mallId", value))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_ID"));
	}

	private HospitalTreatment hospitalTreatment(Hospital targetHospital, Treatment treatment, int regularPrice) {
		return HospitalTreatment.builder()
				.hospital(targetHospital)
				.treatment(treatment)
				.regularPrice(regularPrice)
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
