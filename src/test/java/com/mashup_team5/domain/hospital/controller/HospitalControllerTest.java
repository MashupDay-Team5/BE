package com.mashup_team5.domain.hospital.controller;

import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.mall.entity.Mall;
import com.mashup_team5.domain.mall.entity.MallPrice;
import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.region.entity.RegionType;
import com.mashup_team5.domain.review.entity.Review;
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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.util.LinkedMultiValueMap;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"spring.jpa.properties.hibernate.generate_statistics=true",
		"spring.datasource.url=jdbc:h2:mem:hospital-search-test;MODE=MySQL;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER"
})
@AutoConfigureMockMvc
@Transactional
class HospitalControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private Region seoul;
	private Region gangnam;
	private Region seocho;
	private Region seongnam;
	private TreatmentCategory visionCorrection;
	private TreatmentCategory skinTreatment;
	private Treatment lasik;
	private Treatment smileLasik;
	private Treatment botox;
	private Mall mall;
	private MedicalCategory medicalCategory;
	private Hospital firstHospital;
	private Hospital secondHospital;
	private Hospital thirdHospital;
	private Hospital fourthHospital;

	@BeforeEach
	void setUp() {
		seoul = persist(Region.builder().name("서울").regionType(RegionType.SIDO).build());
		Region gyeonggi = persist(Region.builder().name("경기도").regionType(RegionType.SIDO).build());
		gangnam = persist(Region.builder()
				.parentRegion(seoul).name("강남구").regionType(RegionType.SIGUNGU).build());
		seocho = persist(Region.builder()
				.parentRegion(seoul).name("서초구").regionType(RegionType.SIGUNGU).build());
		seongnam = persist(Region.builder()
				.parentRegion(gyeonggi).name("성남시").regionType(RegionType.SIGUNGU).build());
		Region gangnamDong = persist(Region.builder()
				.parentRegion(gangnam).name("역삼동").regionType(RegionType.DONG).build());
		Region seochoDong = persist(Region.builder()
				.parentRegion(seocho).name("서초4동").regionType(RegionType.DONG).build());
		Region seongnamDong = persist(Region.builder()
				.parentRegion(seongnam).name("정자동").regionType(RegionType.DONG).build());

		medicalCategory = persist(MedicalCategory.builder().name("테스트 진료").build());
		visionCorrection = persist(TreatmentCategory.builder()
				.medicalCategory(medicalCategory).name("시력교정술").build());
		skinTreatment = persist(TreatmentCategory.builder()
				.medicalCategory(medicalCategory).name("피부시술").build());
		lasik = persist(Treatment.builder()
				.treatmentCategory(visionCorrection).name("라식").displayOrder(1).build());
		smileLasik = persist(Treatment.builder()
				.treatmentCategory(visionCorrection).name("스마일라식").displayOrder(2).build());
		botox = persist(Treatment.builder()
				.treatmentCategory(skinTreatment).name("보톡스").displayOrder(1).build());
		mall = persist(Mall.builder().name("테스트몰").build());

		firstHospital = persist(hospitalBuilder("가병원", gangnamDong, true, true, true, true, 100)
				.rating(new BigDecimal("4.7")).reviewCount(120).isPopular(true).isOpenToday(true)
				.hasSpecialOffer(true).address("서울특별시 강남구 테헤란로 123").build());
		secondHospital = persist(hospitalBuilder("나병원", seochoDong, false, true, true, false, 100)
				.rating(new BigDecimal("3.9")).reviewCount(300).isPopular(false).isOpenToday(false)
				.hasSpecialOffer(false).build());
		thirdHospital = persist(hospitalBuilder("다병원", seongnamDong, true, false, false, true, 50)
				.rating(new BigDecimal("4.7")).reviewCount(120).build());
		fourthHospital = persist(hospitalBuilder("라병원", gangnamDong, false, false, false, false, null).build());

		HospitalTreatment firstLasik = persist(hospitalTreatment(firstHospital, lasik, 100000));
		persist(hospitalTreatment(firstHospital, botox, 15000000));
		persist(hospitalTreatment(secondHospital, lasik, 150000));
		persist(hospitalTreatment(secondHospital, smileLasik, 50001));
		persist(hospitalTreatment(secondHospital, botox, 90000));
		persist(hospitalTreatment(thirdHospital, botox, 80000));
		persist(hospitalTreatment(fourthHospital, smileLasik, 0));
		persist(MallPrice.builder().hospitalTreatment(firstLasik).mall(mall).price(70000).build());

		persist(review(firstHospital, "5.0"));
		persist(review(firstHospital, "4.0"));
		persist(review(secondHospital, "5.0"));
		persist(review(thirdHospital, "3.0"));
		persist(review(thirdHospital, "3.0"));
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("필터가 없으면 방문 수와 ID 순으로 중복 없이 페이지 조회한다")
	void getHospitalsWithoutFilters() throws Exception {
		Statistics statistics = statistics();
		statistics.clear();

		mockMvc.perform(get("/api/hospitals").param("size", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.content.length()").value(2))
				.andExpect(jsonPath("$.data.content[*].hospitalId").value(contains(
						firstHospital.getId().intValue(), secondHospital.getId().intValue())))
				.andExpect(jsonPath("$.data.page").value(0))
				.andExpect(jsonPath("$.data.size").value(2))
				.andExpect(jsonPath("$.data.hasNext").value(true))
				.andExpect(jsonPath("$.data.content[0].rating").value(4.7))
				.andExpect(jsonPath("$.data.content[0].reviewCount").value(120))
				.andExpect(jsonPath("$.data.content[0].address").value("서울특별시 강남구 테헤란로 123"))
				.andExpect(jsonPath("$.data.content[0].region.sidoName").value("서울"))
				.andExpect(jsonPath("$.data.content[0].region.sigunguName").value("강남구"))
				.andExpect(jsonPath("$.data.content[0].region.dongName").value("역삼동"))
				.andExpect(jsonPath("$.data.content[0].treatments.length()").value(2));

		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(3);
		org.assertj.core.api.Assertions.assertThat(statistics.getQueries())
				.noneMatch(query -> query.contains("from Review"));

		mockMvc.perform(get("/api/hospitals").param("page", "1").param("size", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[*].hospitalId").value(contains(
						thirdHospital.getId().intValue(), fourthHospital.getId().intValue())))
				.andExpect(jsonPath("$.data.page").value(1))
				.andExpect(jsonPath("$.data.hasNext").value(false));
	}

	@Test
	@DisplayName("시술과 SIDO, SIGUNGU, 복수 지역 OR 조건을 관계 ID로 필터링한다")
	void filterByTreatmentAndRegions() throws Exception {
		assertHospitalIds(get("/api/hospitals")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("treatmentIds", lasik.getId().toString()),
				firstHospital, secondHospital);
		assertHospitalIds(get("/api/hospitals").param("regionIds", seoul.getId().toString()),
				firstHospital, secondHospital, fourthHospital);
		assertHospitalIds(get("/api/hospitals").param("regionIds", seongnam.getId().toString()),
				thirdHospital);
		assertHospitalIds(get("/api/hospitals").param(
						"regionIds", gangnam.getId().toString(), seongnam.getId().toString()),
				firstHospital, thirdHospital, fourthHospital);
	}

	@Test
	@DisplayName("전체 선택은 해당 시술카테고리 병원과 가격 카드만 반환한다")
	void filterAndRespondByTreatmentCategory() throws Exception {
		mockMvc.perform(get("/api/hospitals")
						.param("treatmentCategoryId", visionCorrection.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[*].hospitalId").value(contains(
						firstHospital.getId().intValue(),
						secondHospital.getId().intValue(),
						fourthHospital.getId().intValue())))
				.andExpect(jsonPath("$.data.content[0].treatments.length()").value(1))
				.andExpect(jsonPath("$.data.content[0].treatments[0].treatmentName").value("라식"))
				.andExpect(jsonPath("$.data.content[1].treatments[*].treatmentName")
						.value(contains("라식", "스마일라식")));

		mockMvc.perform(get("/api/hospitals")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("treatmentIds", lasik.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[*].hospitalId").value(contains(
						firstHospital.getId().intValue(), secondHospital.getId().intValue())))
				.andExpect(jsonPath("$.data.content[0].treatments.length()").value(1))
				.andExpect(jsonPath("$.data.content[1].treatments.length()").value(1));

		mockMvc.perform(get("/api/hospitals")
						.param("treatmentCategoryId", skinTreatment.getId().toString())
						.param("treatmentIds", lasik.getId().toString()))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("CATEGORY_MISMATCH"));
	}

	@Test
	@DisplayName("선택한 진료 조건은 AND로 적용하고 선택하지 않은 조건은 제외한다")
	void filterByHospitalOptions() throws Exception {
		assertHospitalIds(get("/api/hospitals").param("hasSpecialist", "true"),
				firstHospital, thirdHospital);
		assertHospitalIds(get("/api/hospitals").param("hasPublicPrice", "true"),
				firstHospital, secondHospital);
		assertHospitalIds(get("/api/hospitals").param("hasNightClinic", "true"),
				firstHospital, secondHospital);
		assertHospitalIds(get("/api/hospitals").param("hasHolidayClinic", "true"),
				firstHospital, thirdHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("hasSpecialist", "true")
				.param("hasPublicPrice", "true")
				.param("hasNightClinic", "true")
				.param("hasHolidayClinic", "true"), firstHospital);
	}

	@Test
	@DisplayName("정상가 기준 최소가, 최대가, 가격 범위와 시술 미지정 가격 조건을 처리한다")
	void filterByRegularPrice() throws Exception {
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", visionCorrection.getId().toString())
				.param("treatmentIds", lasik.getId().toString())
				.param("minPrice", "120000"), secondHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", visionCorrection.getId().toString())
				.param("treatmentIds", lasik.getId().toString())
				.param("maxPrice", "120000"), firstHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", visionCorrection.getId().toString())
				.param("treatmentIds", lasik.getId().toString())
				.param("minPrice", "90000")
				.param("maxPrice", "120000"), firstHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", skinTreatment.getId().toString())
				.param("maxPrice", "85000"), thirdHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", visionCorrection.getId().toString())
				.param("minPrice", "1")
				.param("maxPrice", "60000"), secondHospital);

		mockMvc.perform(get("/api/hospitals")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("minPrice", "1")
						.param("maxPrice", "60000"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].treatments[*].treatmentId")
						.value(contains(smileLasik.getId().intValue())));
	}

	@Test
	@DisplayName("시술과 몰 가격 범위를 적용하고 몰 가격이 있으면 정상가로 대체하지 않는다")
	void filterByTreatmentAndEffectiveMallPriceRange() throws Exception {
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentIds", lasik.getId().toString())
				.param("minPrice", "90000")
				.param("maxPrice", "120000"), firstHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentIds", lasik.getId().toString())
				.param("mallId", mall.getId().toString())
				.param("minPrice", "60000")
				.param("maxPrice", "80000"), firstHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentIds", lasik.getId().toString())
				.param("mallId", mall.getId().toString())
				.param("minPrice", "100000")
				.param("maxPrice", "160000"), secondHospital);
	}

	@Test
	@DisplayName("몰 가격이 있으면 우선 적용하고 없으면 정상가를 사용한다")
	void filterAndRespondWithEffectiveMallPrice() throws Exception {
		Statistics statistics = statistics();
		statistics.clear();

		mockMvc.perform(get("/api/hospitals")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("treatmentIds", lasik.getId().toString())
						.param("mallId", mall.getId().toString())
						.param("maxPrice", "80000"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.length()").value(1))
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(firstHospital.getId()))
				.andExpect(jsonPath("$.data.content[0].treatments[0].regularPrice").value(100000))
				.andExpect(jsonPath("$.data.content[0].treatments[0].discountPrice").value(70000))
				.andExpect(jsonPath("$.data.content[0].treatments[0].displayPrice").value(70000))
				.andExpect(jsonPath("$.data.content[0].treatments[0].priceType").value("MALL"));
		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(7);

		mockMvc.perform(get("/api/hospitals")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("treatmentIds", lasik.getId().toString())
						.param("mallId", mall.getId().toString())
						.param("minPrice", "100000")
						.param("maxPrice", "160000"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.length()").value(1))
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(secondHospital.getId()))
				.andExpect(jsonPath("$.data.content[0].treatments[0].discountPrice").doesNotExist())
				.andExpect(jsonPath("$.data.content[0].treatments[0].displayPrice").value(150000))
				.andExpect(jsonPath("$.data.content[0].treatments[0].priceType").value("REGULAR"));

		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", skinTreatment.getId().toString())
				.param("mallId", mall.getId().toString())
				.param("maxPrice", "85000"), thirdHospital);
	}

	@Test
	@DisplayName("평점, 리뷰 수, 방문 수, 낮은 가격 정렬과 ID tie-breaker를 적용한다")
	void sortHospitals() throws Exception {
		assertHospitalIds(get("/api/hospitals").param("sort", "RATING"),
				firstHospital, thirdHospital, secondHospital, fourthHospital);
		assertHospitalIds(get("/api/hospitals").param("sort", "REVIEW_COUNT"),
				secondHospital, firstHospital, thirdHospital, fourthHospital);
		assertHospitalIds(get("/api/hospitals").param("sort", "VISIT"),
				firstHospital, secondHospital, thirdHospital, fourthHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("sort", "LOW_PRICE")
				.param("treatmentCategoryId", visionCorrection.getId().toString())
				.param("treatmentIds", lasik.getId().toString())
				.param("mallId", mall.getId().toString()), firstHospital, secondHospital);
	}

	@Test
	@DisplayName("잘못된 가격 범위, 낮은 가격 정렬 조건, 페이지, 지역 개수 요청은 Bad Request로 처리한다")
	void rejectInvalidConditions() throws Exception {
		assertBadRequest(get("/api/hospitals").param("minPrice", "200").param("maxPrice", "100"), "INVALID_PRICE_RANGE");
		assertBadRequest(get("/api/hospitals").param("sort", "LOW_PRICE"), "SORT_REQUIRES_TREATMENT");
		assertBadRequest(get("/api/hospitals").param("sort", "SATISFACTION"), "INVALID_SORT");
		assertBadRequest(get("/api/hospitals").param("minPrice", "-1"));
		assertBadRequest(get("/api/hospitals").param("maxPrice", "15000001"));
		assertBadRequest(get("/api/hospitals").param("page", "-1"), "INVALID_PAGINATION");
		assertBadRequest(get("/api/hospitals").param("size", "51"), "INVALID_PAGINATION");

		MockHttpServletRequestBuilder tooManyRegions = get("/api/hospitals");
		for (long id = 1; id <= 6; id++) {
			tooManyRegions.param("regionIds", String.valueOf(id));
		}
		assertBadRequest(tooManyRegions);
	}

	@Test
	@DisplayName("가격 경계값과 50만원 단위가 아닌 가격을 허용한다")
	void acceptPriceBoundariesAndNonSliderUnits() throws Exception {
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", visionCorrection.getId().toString())
				.param("minPrice", "0")
				.param("maxPrice", "0"), fourthHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", skinTreatment.getId().toString())
				.param("minPrice", "15000000")
				.param("maxPrice", "15000000"), firstHospital);
		assertHospitalIds(get("/api/hospitals")
				.param("treatmentCategoryId", visionCorrection.getId().toString())
				.param("minPrice", "50001")
				.param("maxPrice", "50001"), secondHospital);
	}

	@Test
	@DisplayName("목록과 개수 조회는 같은 필터를 사용하고 결과가 없으면 빈 목록과 0을 반환한다")
	void useSameConditionForListAndCount() throws Exception {
		Statistics statistics = statistics();
		MockHttpServletRequestBuilder listRequest = get("/api/hospitals")
				.param("hasSpecialist", "true")
				.param("regionIds", seoul.getId().toString());
		mockMvc.perform(listRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.length()").value(1))
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(firstHospital.getId()));
		statistics.clear();
		mockMvc.perform(get("/api/hospitals/count")
						.param("hasSpecialist", "true")
						.param("regionIds", seoul.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(1));
		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);

		statistics.clear();
		mockMvc.perform(get("/api/hospitals")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("minPrice", "1000000"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content").isEmpty())
				.andExpect(jsonPath("$.data.hasNext").value(false));
		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);
		entityManager.clear();
		statistics.clear();
		mockMvc.perform(get("/api/hospitals/count")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("minPrice", "1000000"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(0));
		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);
	}

	@Test
	@DisplayName("진료과목 전체 선택은 하위 시술만 조회하고 다른 진료과목 병원을 제외한다")
	void filterByMedicalCategory() throws Exception {
		MedicalCategory otherMedical = persist(MedicalCategory.builder().name("다른 진료").build());
		TreatmentCategory otherCategory = persist(TreatmentCategory.builder()
				.medicalCategory(otherMedical).name("다른 카테고리").build());
		Treatment otherTreatment = persist(Treatment.builder().treatmentCategory(otherCategory).name("다른 시술").build());
		Hospital otherHospital = persist(Hospital.builder().region(seoul).name("다른 진료 병원").build());
		persist(hospitalTreatment(otherHospital, otherTreatment, 10000));
		persist(hospitalTreatment(firstHospital, otherTreatment, 10000));
		entityManager.flush();
		entityManager.clear();

		assertSearchAndCount(Map.of("medicalCategoryId", List.of(medicalCategory.getId().toString())),
				firstHospital, secondHospital, thirdHospital, fourthHospital);
		mockMvc.perform(get("/api/hospitals").param("medicalCategoryId", medicalCategory.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].treatments[*].treatmentId")
						.value(contains(lasik.getId().intValue(), botox.getId().intValue())));
	}

	@Test
	@DisplayName("복수 시술은 OR로 조회하고 모든 상위 조건 및 지역·진료 필터를 함께 적용한다")
	void filterByMultipleTreatmentsAndParents() throws Exception {
		assertSearchAndCount(Map.of(
				"medicalCategoryId", List.of(medicalCategory.getId().toString()),
				"treatmentCategoryId", List.of(visionCorrection.getId().toString())),
				firstHospital, secondHospital, fourthHospital);
		assertSearchAndCount(Map.of("treatmentIds", List.of(lasik.getId().toString(), botox.getId().toString())),
				firstHospital, secondHospital, thirdHospital);
		assertSearchAndCount(Map.of(
				"medicalCategoryId", List.of(medicalCategory.getId().toString()),
				"treatmentCategoryId", List.of(visionCorrection.getId().toString()),
				"treatmentIds", List.of(lasik.getId().toString(), smileLasik.getId().toString())),
				firstHospital, secondHospital, fourthHospital);
		assertSearchAndCount(Map.of(
				"medicalCategoryId", List.of(medicalCategory.getId().toString()),
				"treatmentIds", List.of(lasik.getId().toString(), botox.getId().toString()),
				"regionIds", List.of(seoul.getId().toString(), seongnam.getId().toString()),
				"hasSpecialist", List.of("true"), "hasNightClinic", List.of("true")), firstHospital);
	}

	@Test
	@DisplayName("중복 시술은 한 번만 검증하고 병원과 시술 응답을 중복시키지 않는다")
	void deduplicateTreatmentIds() throws Exception {
		Statistics statistics = statistics();
		statistics.clear();
		mockMvc.perform(get("/api/hospitals").param("treatmentIds",
						lasik.getId().toString(), lasik.getId().toString(), smileLasik.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.length()").value(3))
				.andExpect(jsonPath("$.data.content[1].treatments[*].treatmentId")
						.value(contains(lasik.getId().intValue(), smileLasik.getId().intValue())));
		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(4);
		assertSearchAndCount(Map.of("treatmentIds", List.of(lasik.getId().toString(), lasik.getId().toString())),
				firstHospital, secondHospital);
	}

	@ParameterizedTest
	@ValueSource(strings = {"/api/hospitals", "/api/hospitals/count"})
	@DisplayName("존재하지 않는 카테고리·시술·지역·몰과 일부만 유효한 배열을 구분한다")
	void rejectUnknownFilterIds(String path) throws Exception {
		for (String field : List.of("medicalCategoryId", "treatmentCategoryId")) {
			mockMvc.perform(get(path).param(field, "999999"))
					.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
		}
		mockMvc.perform(get(path).param("treatmentIds", "999999"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_FILTER"));
		mockMvc.perform(get(path).param("treatmentIds", lasik.getId().toString(), "999999"))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_FILTER"));
		mockMvc.perform(get(path).param("regionIds", seoul.getId().toString(), "999999"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("REGION_NOT_FOUND"));
		mockMvc.perform(get(path).param("mallId", "999999"))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("MALL_NOT_FOUND"));
		assertBadRequest(get(path).param("regionIds", seoul.getId().toString(), seoul.getId().toString(),
				seoul.getId().toString(), seoul.getId().toString(), seoul.getId().toString(), seoul.getId().toString()));
		assertBadRequest(get(path).param("minPrice", "200").param("maxPrice", "100"), "INVALID_PRICE_RANGE");
	}

	@ParameterizedTest
	@ValueSource(strings = {"/api/hospitals", "/api/hospitals/count"})
	@DisplayName("카테고리와 선택한 모든 시술의 포함 관계를 검증한다")
	void rejectMismatchedCategories(String path) throws Exception {
		MedicalCategory otherMedical = persist(MedicalCategory.builder().name("다른 진료").build());
		TreatmentCategory otherCategory = persist(TreatmentCategory.builder()
				.medicalCategory(otherMedical).name("다른 시술 카테고리").build());
		Treatment otherTreatment = persist(Treatment.builder().treatmentCategory(otherCategory).name("다른 시술").build());
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(get(path).param("medicalCategoryId", otherMedical.getId().toString())
						.param("treatmentCategoryId", visionCorrection.getId().toString()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("CATEGORY_MISMATCH"));
		mockMvc.perform(get(path).param("medicalCategoryId", otherMedical.getId().toString())
						.param("treatmentIds", lasik.getId().toString(), botox.getId().toString()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("CATEGORY_MISMATCH"));
		mockMvc.perform(get(path).param("medicalCategoryId", medicalCategory.getId().toString())
						.param("treatmentIds", lasik.getId().toString(), otherTreatment.getId().toString()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("CATEGORY_MISMATCH"));
		mockMvc.perform(get(path).param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("treatmentIds", lasik.getId().toString(), botox.getId().toString()))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("CATEGORY_MISMATCH"));
	}

	@ParameterizedTest
	@CsvSource({
		"treatmentIds, 0", "treatmentIds, -1", "treatmentIds, abc", "treatmentIds, ''",
		"treatmentIds, '[1]'", "treatmentIds, 9223372036854775808", "treatmentId, 1",
		"regionIds, ''", "regionIds, 0", "regionIds, abc", "medicalCategoryId, -1",
		"treatmentCategoryId, abc", "medicalCategoryId, ''", "mallId, 0", "mallId, ''",
		"minPrice, -1", "maxPrice, abc", "minPrice, ''", "hasSpecialist, abc",
		"hasPublicPrice, 1", "hasNightClinic, yes", "hasHolidayClinic, ''"
	})
	@DisplayName("목록과 count의 잘못된 숫자·배열·boolean은 INVALID_FILTER로 반환한다")
	void rejectMalformedFilters(String field, String value) throws Exception {
		for (String path : List.of("/api/hospitals", "/api/hospitals/count")) {
			mockMvc.perform(get(path).param(field, value))
					.andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_FILTER"));
		}
	}

	@ParameterizedTest
	@CsvSource({"page, -1", "page, abc", "page, ''", "page, 1000001", "size, 0", "size, 51", "size, abc", "size, ''"})
	@DisplayName("목록의 페이지 바인딩과 범위 오류는 INVALID_PAGINATION을 반환한다")
	void rejectInvalidPagination(String field, String value) throws Exception {
		assertBadRequest(get("/api/hospitals").param(field, value), "INVALID_PAGINATION");
	}

	@Test
	@DisplayName("count는 잘못된 페이지와 정렬 입력도 검색 조건으로 사용하지 않는다")
	void countIgnoresPaginationAndSort() throws Exception {
		for (String sort : List.of("LOW_PRICE", "invalid", "")) {
			mockMvc.perform(get("/api/hospitals/count").param("sort", sort).param("page", "abc").param("size", "-1"))
					.andExpect(status().isOk()).andExpect(jsonPath("$.data").value(4));
		}
	}

	@Test
	@DisplayName("정렬 오류와 LOW_PRICE 시술 미선택을 명세 코드로 구분한다")
	void rejectInvalidSortAndMissingTreatments() throws Exception {
		assertBadRequest(get("/api/hospitals").param("sort", ""), "INVALID_SORT");
		assertBadRequest(get("/api/hospitals").param("sort", "invalid"), "INVALID_SORT");
		assertBadRequest(get("/api/hospitals").param("sort", "LOW_PRICE").param("treatmentIds", ""), "SORT_REQUIRES_TREATMENT");
		assertBadRequest(get("/api/hospitals").param("sort", "LOW_PRICE")
				.param("medicalCategoryId", medicalCategory.getId().toString()), "SORT_REQUIRES_TREATMENT");
		assertBadRequest(get("/api/hospitals").param("sort", "LOW_PRICE")
				.param("treatmentCategoryId", visionCorrection.getId().toString()), "SORT_REQUIRES_TREATMENT");
	}

	@Test
	@DisplayName("유효한 ID와 빈 값이 섞인 시술 배열도 전체 조회로 바꾸지 않는다")
	void rejectPartiallyEmptyTreatmentIds() throws Exception {
		for (String path : List.of("/api/hospitals", "/api/hospitals/count")) {
			assertBadRequest(get(path).param("treatmentIds", lasik.getId().toString(), ""));
			assertBadRequest(get(path).param("treatmentIds", lasik.getId() + ",," + smileLasik.getId()));
		}
	}

	private void assertSearchAndCount(Map<String, List<String>> filters, Hospital... hospitals) throws Exception {
		assertHospitalIds(get("/api/hospitals").params(new LinkedMultiValueMap<>(filters)), hospitals);
		mockMvc.perform(get("/api/hospitals/count").params(new LinkedMultiValueMap<>(filters)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").value(hospitals.length));
	}

	@Test
	@DisplayName("복수 시술과 가격 범위는 같은 상품에 적용하며 응답 상품도 가격 범위로 제한한다")
	void filterMultipleTreatmentsByEffectivePrice() throws Exception {
		assertSearchAndCount(Map.of(
				"treatmentIds", List.of(lasik.getId().toString(), smileLasik.getId().toString()),
				"minPrice", List.of("50000"), "maxPrice", List.of("60000")), secondHospital);
		mockMvc.perform(get("/api/hospitals").param("treatmentIds", lasik.getId().toString(), smileLasik.getId().toString())
						.param("minPrice", "50000").param("maxPrice", "60000"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].treatments[*].treatmentId").value(contains(smileLasik.getId().intValue())));
		assertSearchAndCount(Map.of("treatmentIds", List.of(lasik.getId().toString()), "maxPrice", List.of("90000")));
		assertSearchAndCount(Map.of(
				"treatmentIds", List.of(lasik.getId().toString(), smileLasik.getId().toString()),
				"minPrice", List.of("0"), "maxPrice", List.of("0")), fourthHospital);
	}

	@Test
	@DisplayName("복수 시술에서도 상품별 몰 가격을 우선하고 몰 가격이 없는 상품만 정상가로 대체한다")
	void filterMultipleTreatmentsWithMallFallback() throws Exception {
		assertSearchAndCount(Map.of(
				"treatmentIds", List.of(lasik.getId().toString(), botox.getId().toString()),
				"mallId", List.of(mall.getId().toString()),
				"minPrice", List.of("60000"), "maxPrice", List.of("80000")), firstHospital, thirdHospital);
		assertSearchAndCount(Map.of(
				"treatmentIds", List.of(lasik.getId().toString(), smileLasik.getId().toString()),
				"mallId", List.of(mall.getId().toString()),
				"minPrice", List.of("90000"), "maxPrice", List.of("120000")));
	}

	@Test
	@DisplayName("LOW_PRICE는 선택한 복수 시술의 최저 정상가를 사용하고 제공하지 않는 시술은 제외한다")
	void sortByMinimumSelectedTreatmentPrice() throws Exception {
		assertHospitalIds(get("/api/hospitals").param("sort", "LOW_PRICE")
				.param("treatmentIds", lasik.getId().toString(), smileLasik.getId().toString()),
				fourthHospital, secondHospital, firstHospital);
		assertHospitalIds(get("/api/hospitals").param("sort", "LOW_PRICE")
				.param("treatmentIds", lasik.getId().toString(), botox.getId().toString()),
				thirdHospital, secondHospital, firstHospital);
	}

	@Test
	@DisplayName("LOW_PRICE는 각 시술의 몰 가격 fallback 후 최저가를 구한다")
	void sortAfterApplyingMallFallbackPerTreatment() throws Exception {
		persist(hospitalTreatment(firstHospital, smileLasik, 30000));
		entityManager.flush();
		entityManager.clear();
		Statistics statistics = statistics();
		statistics.clear();

		assertHospitalIds(get("/api/hospitals").param("sort", "LOW_PRICE")
				.param("mallId", mall.getId().toString())
				.param("treatmentIds", lasik.getId().toString(), smileLasik.getId().toString()),
				fourthHospital, firstHospital, secondHospital);
		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(6);
	}

	@Test
	@DisplayName("복수 시술 LOW_PRICE 정렬에서 동률은 병원 ID 오름차순이다")
	void breakMinimumPriceTiesByHospitalId() throws Exception {
		persist(hospitalTreatment(firstHospital, smileLasik, 50001));
		entityManager.flush();
		entityManager.clear();

		assertHospitalIds(get("/api/hospitals").param("sort", "LOW_PRICE")
				.param("treatmentIds", lasik.getId().toString(), smileLasik.getId().toString()),
				fourthHospital, firstHospital, secondHospital);
	}

	@Test
	@DisplayName("Swagger는 복수 시술과 count 전용 필터 및 병원 API 오류 명세를 생성한다")
	void documentHospitalApiContract() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.paths['/api/hospitals'].get.parameters[?(@.name=='medicalCategoryId')]").isNotEmpty())
				.andExpect(jsonPath("$.paths['/api/hospitals'].get.parameters[?(@.name=='treatmentIds')].schema.type")
						.value(contains("array")))
				.andExpect(jsonPath("$.paths['/api/hospitals'].get.parameters[?(@.name=='treatmentIds')].schema.items.type")
						.value(contains("integer")))
				.andExpect(jsonPath("$.paths['/api/hospitals'].get.parameters[?(@.name=='treatmentId')]").isEmpty())
				.andExpect(jsonPath("$.paths['/api/hospitals/count'].get.parameters[?(@.name=='page'||@.name=='size'||@.name=='sort')]").isEmpty())
				.andExpect(jsonPath("$.paths['/api/hospitals/count'].get.parameters[?(@.name=='treatmentIds')].schema.type")
						.value(contains("array")))
				.andExpect(jsonPath("$.paths['/api/hospitals'].get.responses['200']").exists())
				.andExpect(jsonPath("$.paths['/api/hospitals'].get.responses['400'].description")
						.value(org.hamcrest.Matchers.containsString("SORT_REQUIRES_TREATMENT")))
				.andExpect(jsonPath("$.paths['/api/hospitals/count'].get.responses['404'].description")
						.value(org.hamcrest.Matchers.containsString("CATEGORY_NOT_FOUND")))
				.andExpect(jsonPath("$.paths['/api/hospitals/{hospitalId}/treatments/{treatmentId}'].get.parameters[*].name")
						.value(org.hamcrest.Matchers.containsInAnyOrder("hospitalId", "treatmentId", "mallId")))
				.andExpect(jsonPath("$.paths['/api/hospitals/{hospitalId}/treatments/{treatmentId}'].get.parameters[*].schema.type")
						.value(contains("integer", "integer", "integer")))
				.andExpect(jsonPath("$.paths['/api/hospitals/{hospitalId}/treatments/{treatmentId}'].get.responses['404'].description")
						.value(org.hamcrest.Matchers.allOf(
								org.hamcrest.Matchers.containsString("MALL_NOT_FOUND"),
								org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("MALL_OFFER_NOT_FOUND")))))
				.andExpect(jsonPath("$.paths['/api/hospitals/{hospitalId}/treatments/{treatmentId}'].get.description")
						.value(org.hamcrest.Matchers.containsString("discountPrice=null")));
	}

	@Test
	@DisplayName("여러 시술과 리뷰가 있어도 병원과 리뷰 수 및 count가 부풀려지지 않는다")
	void avoidJoinMultiplication() throws Exception {
		mockMvc.perform(get("/api/hospitals"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content.length()").value(4))
				.andExpect(jsonPath("$.data.content[0].reviewCount").value(120));
		mockMvc.perform(get("/api/hospitals/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(4));
		mockMvc.perform(get("/api/hospitals/count")
						.param("treatmentCategoryId", visionCorrection.getId().toString())
						.param("treatmentIds", lasik.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").value(2));
	}

	@Test
	@DisplayName("실제 리뷰가 추가되어도 병원에 저장된 평점과 리뷰 수를 반환한다")
	void useStoredStatisticsRegardlessOfReviews() throws Exception {
		persist(review(secondHospital, "4.0"));
		persist(review(secondHospital, "4.0"));
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals")
						.param("regionIds", seocho.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(secondHospital.getId()))
				.andExpect(jsonPath("$.data.content[0].rating").value(3.9))
				.andExpect(jsonPath("$.data.content[0].reviewCount").value(300));
	}

	@Test
	@DisplayName("병원 필드만으로 다섯 배지를 조립하고 시술 검색에도 유지한다")
	void getHospitalBadges() throws Exception {
		Statistics statistics = statistics();
		statistics.clear();

		mockMvc.perform(get("/api/hospitals"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].badgeTypes").value(contains(
						"POPULAR", "NIGHT_CLINIC", "SAME_DAY_BOOKING", "OPEN_TODAY", "SPECIAL_OFFER")))
				.andExpect(jsonPath("$.data.content[1].badgeTypes")
						.value(contains("NIGHT_CLINIC", "SAME_DAY_BOOKING")))
				.andExpect(jsonPath("$.data.content[2].badgeTypes").value(contains("SAME_DAY_BOOKING")))
				.andExpect(jsonPath("$.data.content[*].isReservable").doesNotExist())
				.andExpect(jsonPath("$.data.content[*].treatments[*].isSpecialOffer").doesNotExist());

		org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(3);
		mockMvc.perform(get("/api/hospitals").param("treatmentIds", botox.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].badgeTypes")
						.value(contains("POPULAR", "NIGHT_CLINIC", "SAME_DAY_BOOKING", "OPEN_TODAY", "SPECIAL_OFFER")));
	}

	@Test
	@DisplayName("할인 가격이 있어도 병원 특가 배지가 false이면 특가 배지를 반환하지 않는다")
	void excludeSpecialOfferBadgeDespiteDiscountPrice() throws Exception {
		HospitalTreatment hospitalTreatment = entityManager.createQuery("""
				select ht from HospitalTreatment ht
				where ht.hospital.id = :hospitalId and ht.treatment.id = :treatmentId
				""", HospitalTreatment.class)
				.setParameter("hospitalId", secondHospital.getId())
				.setParameter("treatmentId", lasik.getId()).getSingleResult();
		persist(MallPrice.builder().hospitalTreatment(hospitalTreatment).mall(mall).price(60000).build());
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals").param("regionIds", seocho.getId().toString())
						.param("treatmentIds", lasik.getId().toString()).param("mallId", mall.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(secondHospital.getId()))
				.andExpect(jsonPath("$.data.content[0].treatments[0].discountPrice").value(60000))
				.andExpect(jsonPath("$.data.content[0].treatments[0].displayPrice").value(60000))
				.andExpect(jsonPath("$.data.content[0].treatments[0].priceType").value("MALL"))
				.andExpect(jsonPath("$.data.content[0].badgeTypes").value(contains("NIGHT_CLINIC", "SAME_DAY_BOOKING")));
	}

	@Test
	@DisplayName("할인 가격이 없어도 병원 특가 배지가 true이면 특가 배지를 반환한다")
	void includeSpecialOfferBadgeWithoutDiscountPrice() throws Exception {
		mockMvc.perform(get("/api/hospitals").param("treatmentIds", botox.getId().toString())
						.param("mallId", mall.getId().toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(firstHospital.getId()))
				.andExpect(jsonPath("$.data.content[0].treatments[0].discountPrice").doesNotExist())
				.andExpect(jsonPath("$.data.content[0].treatments[0].displayPrice").value(15000000))
				.andExpect(jsonPath("$.data.content[0].treatments[0].priceType").value("REGULAR"))
				.andExpect(jsonPath("$.data.content[0].badgeTypes").value(contains(
						"POPULAR", "NIGHT_CLINIC", "SAME_DAY_BOOKING", "OPEN_TODAY", "SPECIAL_OFFER")));
	}

	@Test
	@DisplayName("조회할 시술상품이 없어도 병원 특가 배지를 반환한다")
	void includeSpecialOfferBadgeWithoutTreatments() throws Exception {
		Hospital hospital = persist(Hospital.builder().region(seoul).name("특가 병원")
				.hasSpecialOffer(true).monthlyVisitCount(1000).build());
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals").param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(hospital.getId()))
				.andExpect(jsonPath("$.data.content[0].treatments").isEmpty())
				.andExpect(jsonPath("$.data.content[0].badgeTypes").value(contains("SPECIAL_OFFER")));
	}

	@ParameterizedTest
	@CsvSource({
			"true, false, false, false, false, POPULAR",
			"false, true, false, false, false, NIGHT_CLINIC",
			"false, false, true, false, false, SAME_DAY_BOOKING",
			"false, false, false, true, false, OPEN_TODAY",
			"false, false, false, false, true, SPECIAL_OFFER"
	})
	@DisplayName("각 원본 필드가 true인 경우에만 대응하는 배지를 반환한다")
	void mapEachBadgeIndependently(
			boolean popular, boolean nightClinic, boolean reservable, boolean openToday,
			boolean specialOffer, String expectedBadge) throws Exception {
		Hospital hospital = persist(Hospital.builder().region(seoul).name("단일 배지 병원")
				.isPopular(popular).hasNightClinic(nightClinic).isReservable(reservable)
				.isOpenToday(openToday).hasSpecialOffer(specialOffer).monthlyVisitCount(1000).build());
		persist(HospitalTreatment.builder().hospital(hospital).treatment(lasik).build());
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals").param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(hospital.getId()))
				.andExpect(jsonPath("$.data.content[0].badgeTypes").value(contains(expectedBadge)));
	}

	@Test
	@DisplayName("신규 필드와 배지 원본 값이 null이면 통계는 0, 배지는 빈 목록을 반환한다")
	void getHospitalsWithNullFields() throws Exception {
		Hospital legacyHospital = persist(Hospital.builder().region(seocho).name("기존 병원").build());
		persist(HospitalTreatment.builder().hospital(legacyHospital).treatment(lasik).build());
		persist(review(legacyHospital, "5.0"));
		entityManager.flush();
		entityManager.clear();

		mockMvc.perform(get("/api/hospitals").param("page", "4").param("size", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.content[0].hospitalId").value(legacyHospital.getId()))
				.andExpect(jsonPath("$.data.content[0].rating").value(0.0))
				.andExpect(jsonPath("$.data.content[0].reviewCount").value(0))
				.andExpect(jsonPath("$.data.content[0].address").value(org.hamcrest.Matchers.nullValue()))
				.andExpect(jsonPath("$.data.content[0].badgeTypes").isEmpty());
	}

	@Test
	@DisplayName("평점과 리뷰 수 정렬에서 null과 0은 동률로 ID 순서를 유지한다")
	void sortNullStatisticsAsZero() throws Exception {
		Hospital zeroHospital = persist(Hospital.builder().region(seocho).name("통계 0 병원")
				.rating(BigDecimal.ZERO).reviewCount(0).build());
		entityManager.flush();
		entityManager.clear();

		assertHospitalIds(get("/api/hospitals").param("sort", "RATING"),
				firstHospital, thirdHospital, secondHospital, fourthHospital, zeroHospital);
		assertHospitalIds(get("/api/hospitals").param("sort", "REVIEW_COUNT"),
				secondHospital, firstHospital, thirdHospital, fourthHospital, zeroHospital);
	}

	private Hospital.HospitalBuilder hospitalBuilder(
			String name,
			Region region,
			boolean hasSpecialist,
			boolean hasPublicPrice,
			boolean hasNightClinic,
			boolean hasHolidayClinic,
			Integer monthlyVisitCount) {
		return Hospital.builder()
				.name(name)
				.region(region)
				.hasSpecialist(hasSpecialist)
				.hasPublicPrice(hasPublicPrice)
				.hasNightClinic(hasNightClinic)
				.hasHolidayClinic(hasHolidayClinic)
				.isReservable(true)
				.monthlyVisitCount(monthlyVisitCount);
	}

	private HospitalTreatment hospitalTreatment(Hospital hospital, Treatment treatment, int regularPrice) {
		return HospitalTreatment.builder()
				.hospital(hospital)
				.treatment(treatment)
				.regularPrice(regularPrice)
				.build();
	}

	private Review review(Hospital hospital, String rating) {
		return Review.builder()
				.hospital(hospital)
				.rating(new BigDecimal(rating))
				.build();
	}

	private <T> T persist(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	private void assertHospitalIds(
			MockHttpServletRequestBuilder request,
			Hospital... hospitals) throws Exception {
		Integer[] ids = java.util.Arrays.stream(hospitals)
				.map(Hospital::getId)
				.map(Long::intValue)
				.toArray(Integer[]::new);
		mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(ids.length == 0 ? jsonPath("$.data.content").isEmpty()
						: jsonPath("$.data.content[*].hospitalId").value(contains(ids)));
	}

	private void assertBadRequest(MockHttpServletRequestBuilder request) throws Exception {
		assertBadRequest(request, "INVALID_FILTER");
	}

	private void assertBadRequest(MockHttpServletRequestBuilder request, String code) throws Exception {
		mockMvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.code").value(code));
	}

	private Statistics statistics() {
		return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
	}
}
