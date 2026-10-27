package com.mashup_team5.masterdata;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/db/migration/V2__insert_master_data.sql", config = @SqlConfig(encoding = "UTF-8"))
class MasterDataIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	@DisplayName("진료 기준 데이터가 중복과 전체 옵션 없이 계층 및 노출순서대로 반환된다")
	void getSeededCategories() throws Exception {
		assertThat(count("medical_category")).isEqualTo(20);
		assertThat(count("treatment_category")).isEqualTo(98);
		assertThat(count("treatment")).isEqualTo(577);
		assertThat(countByName("medical_category", "전체")).isZero();
		assertThat(countByName("treatment_category", "전체")).isZero();
		assertThat(countByName("treatment", "전체")).isZero();
		assertThat(jdbcTemplate.queryForObject("""
			SELECT COUNT(*)
			FROM (
				SELECT treatment_category_id, name
				FROM treatment
				GROUP BY treatment_category_id, name
				HAVING COUNT(*) > 1
			) duplicate_treatment
			""", Long.class)).isZero();

		Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		statistics.clear();

		mockMvc.perform(get("/api/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.length()").value(20))
			.andExpect(jsonPath("$.data[0].name").value("안과"))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].name").value("시력교정술"))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments.length()").value(12))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments[0].name")
				.value("시력교정술 검진"))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments[1].name")
				.value("스마일라식"))
			.andExpect(jsonPath("$.data[0].treatmentCategories[0].treatments[7].name")
				.value("스마트라식"));

		assertThat(statistics.getPrepareStatementCount()).isEqualTo(3);
	}

	@Test
	@DisplayName("지역 기준 데이터가 SIDO와 SIGUNGU 2단계로 반환된다")
	void getSeededRegions() throws Exception {
		assertThat(jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM region WHERE region_type = 'SIDO'", Long.class)).isEqualTo(17);
		assertThat(jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM region WHERE region_type = 'SIGUNGU'", Long.class)).isEqualTo(135);
		assertThat(jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM region WHERE region_type = 'DONG'", Long.class)).isZero();
		assertThat(countByName("region", "전체")).isZero();
		assertThat(countByName("region", "중구")).isGreaterThan(1);
		assertThat(jdbcTemplate.queryForObject("""
			SELECT COUNT(*)
			FROM (
				SELECT parent_region_id, name
				FROM region
				WHERE parent_region_id IS NOT NULL
				GROUP BY parent_region_id, name
				HAVING COUNT(*) > 1
			) duplicate_region
			""", Long.class)).isZero();

		mockMvc.perform(get("/api/regions"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(17))
			.andExpect(jsonPath("$.data[*].regionType").value(everyItem(is("SIDO"))));

		Long seoulId = sidoId("서울");
		mockMvc.perform(get("/api/regions").param("parentId", seoulId.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(24))
			.andExpect(jsonPath("$.data[*].regionType").value(everyItem(is("SIGUNGU"))))
			.andExpect(jsonPath("$.data[0].name").value("강남구"))
			.andExpect(jsonPath("$.data[1].name").value("서초구"));

		Long chungbukId = sidoId("충청북도");
		mockMvc.perform(get("/api/regions").param("parentId", chungbukId.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[*].name")
				.value(contains("제천시", "청주시", "충주시")));

		Long chungnamId = sidoId("충청남도");
		mockMvc.perform(get("/api/regions").param("parentId", chungnamId.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[*].name").value(contains(
				"계룡시", "공주시", "논산시", "당진시", "보령시", "서산시", "아산시", "천안시")));

		Long sejongId = sidoId("세종");
		mockMvc.perform(get("/api/regions").param("parentId", sejongId.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	@DisplayName("같은 시술카테고리에 동일한 treatment 이름을 저장할 수 없다")
	void rejectDuplicateTreatmentNameWithinCategory() {
		assertThatThrownBy(() -> jdbcTemplate.update("""
			INSERT INTO treatment (treatment_id, treatment_category_id, name, display_order)
			VALUES (1000, 1, '스마일라식', 100)
			"""))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	private long count(String table) {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
	}

	private long countByName(String table, String name) {
		return jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM " + table + " WHERE name = ?", Long.class, name);
	}

	private Long sidoId(String name) {
		List<Long> ids = jdbcTemplate.queryForList(
			"SELECT region_id FROM region WHERE parent_region_id IS NULL AND name = ?",
			Long.class,
			name
		);
		assertThat(ids).hasSize(1);
		return ids.getFirst();
	}
}
