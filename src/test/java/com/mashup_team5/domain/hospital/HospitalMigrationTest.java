package com.mashup_team5.domain.hospital;

import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.region.entity.Region;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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

	@Test
	@DisplayName("V1부터 V3까지 적용한 스키마에서 병원 시연 필드를 저장하고 조회한다")
	void persistDemoFieldsWithMigratedSchema() {
		assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospital", Long.class)).isZero();
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
