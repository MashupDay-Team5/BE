package com.mashup_team5.domain.hospital;

import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.treatment.dto.CategoryResponse;
import com.mashup_team5.domain.treatment.repository.TreatmentRepository;
import com.mashup_team5.domain.treatment.service.CategoryService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:hospital-migration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER",
	"spring.flyway.enabled=true",
	"spring.jpa.hibernate.ddl-auto=validate"
})
@Transactional
class HospitalMigrationTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private TreatmentRepository treatmentRepository;

	@Test
	@DisplayName("V1부터 V3까지 적용한 스키마에서 병원 시연 필드를 저장하고 조회한다")
	void persistDemoFieldsWithMigratedSchema() {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital", Long.class)).isEqualTo(63);
		Region region = entityManager.createQuery("select r from Region r order by r.id", Region.class)
				.setMaxResults(1).getSingleResult();
		Hospital hospital = Hospital.builder().region(region).name("시연 병원")
				.rating(new BigDecimal("4.7")).reviewCount(120).isPopular(true).isOpenToday(false)
				.hasSpecialOffer(true).address("서울특별시 강남구 테헤란로 123").build();
		entityManager.persist(hospital);
		entityManager.flush();
		entityManager.clear();

		Hospital saved = entityManager.find(Hospital.class, hospital.getId());
		assertThat(saved.getRating()).isEqualByComparingTo("4.7");
		assertThat(saved.getReviewCount()).isEqualTo(120);
		assertThat(saved.getIsPopular()).isTrue();
		assertThat(saved.getIsOpenToday()).isFalse();
		assertThat(saved.getHasSpecialOffer()).isTrue();
		assertThat(saved.getAddress()).isEqualTo("서울특별시 강남구 테헤란로 123");
	}

	@Test
	@DisplayName("V5 목 데이터의 건수와 가격 관계를 보존한다")
	void verifyMockData() {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital_treatment", Long.class)).isEqualTo(472);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mall WHERE mall_id = 1 AND name = '세오스대학교'", Long.class)).isEqualTo(1);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mall_price", Long.class)).isEqualTo(388);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT name) FROM hospital", Long.class)).isEqualTo(63);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital_treatment WHERE regular_price = 0", Long.class)).isEqualTo(10);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital_treatment ht LEFT JOIN mall_price mp ON mp.hospital_treatment_id = ht.hospital_treatment_id WHERE mp.mall_price_id IS NULL", Long.class)).isEqualTo(84);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital WHERE is_popular = TRUE", Long.class)).isEqualTo(3);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital WHERE has_night_clinic = TRUE", Long.class)).isEqualTo(17);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital WHERE is_reservable = TRUE", Long.class)).isEqualTo(24);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital WHERE is_open_today = TRUE", Long.class)).isEqualTo(32);
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital WHERE has_special_offer = TRUE", Long.class)).isEqualTo(29);
	}

	@Test
	@DisplayName("선택지에서 비노출 시술과 비노출 시술만 있는 카테고리를 제외한다")
	void hideFilterTreatments() {
		List<CategoryResponse> categories = categoryService.getCategories();
		List<CategoryResponse.TreatmentCategoryResponse> treatmentCategories = categories.stream()
			.flatMap(category -> category.treatmentCategories().stream()).toList();
		List<Long> ids = treatmentCategories.stream().flatMap(category -> category.treatments().stream())
			.map(CategoryResponse.TreatmentResponse::treatmentId).toList();
		List<Long> hidden = List.of(578L, 579L, 580L, 582L, 583L, 584L, 585L, 587L);
		assertThat(ids).doesNotContainAnyElementsOf(hidden).contains(581L, 586L);
		assertThat(treatmentCategories).extracting(CategoryResponse.TreatmentCategoryResponse::treatmentCategoryId)
			.doesNotContain(99L, 100L).contains(2L, 8L, 9L, 101L);
		assertThat(treatmentRepository.findAllById(hidden)).hasSize(8);
	}

	@Test
	@DisplayName("V3 스키마는 시연 필드를 생략한 기존 병원 데이터도 허용한다")
	void persistHospitalWithoutDemoFields() {
		Region region = entityManager.createQuery("select r from Region r order by r.id", Region.class)
				.setMaxResults(1).getSingleResult();
		Hospital hospital = Hospital.builder().region(region).name("기존 병원").build();
		entityManager.persist(hospital);
		entityManager.flush();
		entityManager.clear();

		Hospital saved = entityManager.find(Hospital.class, hospital.getId());
		assertThat(saved.getRating()).isNull();
		assertThat(saved.getReviewCount()).isNull();
		assertThat(saved.getIsPopular()).isNull();
		assertThat(saved.getIsOpenToday()).isNull();
		assertThat(saved.getHasSpecialOffer()).isNull();
		assertThat(saved.getAddress()).isNull();
	}
}
