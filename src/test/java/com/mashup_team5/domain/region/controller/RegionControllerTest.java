package com.mashup_team5.domain.region.controller;

import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.region.entity.RegionType;
import com.mashup_team5.domain.region.repository.RegionRepository;
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
class RegionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RegionRepository regionRepository;

	@Test
	@DisplayName("parentId가 없으면 최상위 지역만 반환한다")
	void getTopLevelRegions() throws Exception {
		Region seoul = regionRepository.save(Region.builder()
			.name("서울특별시")
			.regionType(RegionType.SIDO)
			.build());
		regionRepository.save(Region.builder()
			.parentRegion(seoul)
			.name("서초구")
			.regionType(RegionType.SIGUNGU)
			.build());

		mockMvc.perform(get("/api/regions"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.length()").value(1))
			.andExpect(jsonPath("$.data[0].regionId").value(seoul.getId()))
			.andExpect(jsonPath("$.data[0].name").value("서울특별시"))
			.andExpect(jsonPath("$.data[0].regionType").value("SIDO"));
	}

	@Test
	@DisplayName("parentId가 있으면 바로 아래 자식 지역만 ID 오름차순으로 반환한다")
	void getChildRegions() throws Exception {
		Region seoul = regionRepository.save(Region.builder()
			.name("서울특별시")
			.regionType(RegionType.SIDO)
			.build());
		Region gangnam = regionRepository.save(Region.builder()
			.parentRegion(seoul)
			.name("강남구")
			.regionType(RegionType.SIGUNGU)
			.build());
		Region seocho = regionRepository.save(Region.builder()
			.parentRegion(seoul)
			.name("서초구")
			.regionType(RegionType.SIGUNGU)
			.build());
		regionRepository.save(Region.builder()
			.parentRegion(seocho)
			.name("서초동")
			.regionType(RegionType.DONG)
			.build());

		mockMvc.perform(get("/api/regions").param("parentId", seoul.getId().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(2))
			.andExpect(jsonPath("$.data[0].regionId").value(gangnam.getId()))
			.andExpect(jsonPath("$.data[0].name").value("강남구"))
			.andExpect(jsonPath("$.data[1].regionId").value(seocho.getId()))
			.andExpect(jsonPath("$.data[1].name").value("서초구"));
	}

	@Test
	@DisplayName("존재하지 않는 parentId이면 Not Found 응답을 반환한다")
	void getRegionsWithUnknownParent() throws Exception {
		mockMvc.perform(get("/api/regions").param("parentId", "999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON404"))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	@DisplayName("자식이 없는 지역을 조회하면 빈 배열을 반환한다")
	void getRegionsWithoutChildren() throws Exception {
		Region seoul = regionRepository.save(Region.builder()
			.name("서울특별시")
			.regionType(RegionType.SIDO)
			.build());

		mockMvc.perform(get("/api/regions").param("parentId", seoul.getId().toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data").isEmpty());
	}
}
