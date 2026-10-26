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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

        MedicalCategory medicalCategory = persist(MedicalCategory.builder().name("테스트 진료").build());
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

        firstHospital = persist(hospital("가병원", gangnamDong, true, true, true, true, 100));
        secondHospital = persist(hospital("나병원", seochoDong, false, true, true, false, 100));
        thirdHospital = persist(hospital("다병원", seongnamDong, true, false, false, true, 50));
        fourthHospital = persist(hospital("라병원", gangnamDong, false, false, false, false, null));

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
                .andExpect(jsonPath("$.data.content[0].rating").value(4.5))
                .andExpect(jsonPath("$.data.content[0].reviewCount").value(2))
                .andExpect(jsonPath("$.data.content[0].region.sidoName").value("서울"))
                .andExpect(jsonPath("$.data.content[0].region.sigunguName").value("강남구"))
                .andExpect(jsonPath("$.data.content[0].region.dongName").value("역삼동"))
                .andExpect(jsonPath("$.data.content[0].treatments.length()").value(2));

        org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(4);

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
                        .param("treatmentId", lasik.getId().toString()),
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
                        .param("treatmentId", lasik.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].hospitalId").value(contains(
                        firstHospital.getId().intValue(), secondHospital.getId().intValue())))
                .andExpect(jsonPath("$.data.content[0].treatments.length()").value(1))
                .andExpect(jsonPath("$.data.content[1].treatments.length()").value(1));

        mockMvc.perform(get("/api/hospitals")
                        .param("treatmentCategoryId", skinTreatment.getId().toString())
                        .param("treatmentId", lasik.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
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
                .param("treatmentId", lasik.getId().toString())
                .param("minPrice", "120000"), secondHospital);
        assertHospitalIds(get("/api/hospitals")
                .param("treatmentCategoryId", visionCorrection.getId().toString())
                .param("treatmentId", lasik.getId().toString())
                .param("maxPrice", "120000"), firstHospital);
        assertHospitalIds(get("/api/hospitals")
                .param("treatmentCategoryId", visionCorrection.getId().toString())
                .param("treatmentId", lasik.getId().toString())
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
                .param("treatmentId", lasik.getId().toString())
                .param("minPrice", "90000")
                .param("maxPrice", "120000"), firstHospital);
        assertHospitalIds(get("/api/hospitals")
                .param("treatmentId", lasik.getId().toString())
                .param("mallId", mall.getId().toString())
                .param("minPrice", "60000")
                .param("maxPrice", "80000"), firstHospital);
        assertHospitalIds(get("/api/hospitals")
                .param("treatmentId", lasik.getId().toString())
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
                        .param("treatmentId", lasik.getId().toString())
                        .param("mallId", mall.getId().toString())
                        .param("maxPrice", "80000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].hospitalId").value(firstHospital.getId()))
                .andExpect(jsonPath("$.data.content[0].treatments[0].regularPrice").value(100000))
                .andExpect(jsonPath("$.data.content[0].treatments[0].discountPrice").value(70000))
                .andExpect(jsonPath("$.data.content[0].treatments[0].displayPrice").value(70000))
                .andExpect(jsonPath("$.data.content[0].treatments[0].priceType").value("MALL"));
        org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(5);

        mockMvc.perform(get("/api/hospitals")
                        .param("treatmentCategoryId", visionCorrection.getId().toString())
                        .param("treatmentId", lasik.getId().toString())
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
                secondHospital, firstHospital, thirdHospital, fourthHospital);
        assertHospitalIds(get("/api/hospitals").param("sort", "REVIEW_COUNT"),
                firstHospital, thirdHospital, secondHospital, fourthHospital);
        assertHospitalIds(get("/api/hospitals").param("sort", "VISIT"),
                firstHospital, secondHospital, thirdHospital, fourthHospital);
        assertHospitalIds(get("/api/hospitals")
                .param("sort", "LOW_PRICE")
                .param("treatmentCategoryId", visionCorrection.getId().toString())
                .param("treatmentId", lasik.getId().toString())
                .param("mallId", mall.getId().toString()), firstHospital, secondHospital);
    }

    @Test
    @DisplayName("잘못된 가격 범위, 낮은 가격 정렬 조건, 페이지 요청은 Bad Request로 처리한다")
    void rejectInvalidConditions() throws Exception {
        assertBadRequest(get("/api/hospitals").param("minPrice", "200").param("maxPrice", "100"));
        assertBadRequest(get("/api/hospitals").param("sort", "LOW_PRICE"));
        assertBadRequest(get("/api/hospitals").param("sort", "SATISFACTION"));
        assertBadRequest(get("/api/hospitals").param("minPrice", "-1"));
        assertBadRequest(get("/api/hospitals").param("maxPrice", "15000001"));
        assertBadRequest(get("/api/hospitals").param("page", "-1"));
        assertBadRequest(get("/api/hospitals").param("size", "101"));
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
        org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);

        statistics.clear();
        mockMvc.perform(get("/api/hospitals")
                        .param("treatmentCategoryId", visionCorrection.getId().toString())
                        .param("minPrice", "1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.hasNext").value(false));
        org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
        statistics.clear();
        mockMvc.perform(get("/api/hospitals/count")
                        .param("treatmentCategoryId", visionCorrection.getId().toString())
                        .param("minPrice", "1000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));
        org.assertj.core.api.Assertions.assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 시술과 리뷰가 있어도 병원과 리뷰 수 및 count가 부풀려지지 않는다")
    void avoidJoinMultiplication() throws Exception {
        mockMvc.perform(get("/api/hospitals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(4))
                .andExpect(jsonPath("$.data.content[0].reviewCount").value(2));
        mockMvc.perform(get("/api/hospitals/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(4));
        mockMvc.perform(get("/api/hospitals/count")
                        .param("treatmentCategoryId", visionCorrection.getId().toString())
                        .param("treatmentId", lasik.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(2));
    }

    private Hospital hospital(
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
                .monthlyVisitCount(monthlyVisitCount)
                .build();
    }

    private HospitalTreatment hospitalTreatment(Hospital hospital, Treatment treatment, int regularPrice) {
        return HospitalTreatment.builder()
                .hospital(hospital)
                .treatment(treatment)
                .regularPrice(regularPrice)
                .isSpecialOffer(false)
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
                .andExpect(jsonPath("$.data.content[*].hospitalId").value(contains(ids)));
    }

    private void assertBadRequest(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("COMMON400"));
    }

    private Statistics statistics() {
        return entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
    }
}
