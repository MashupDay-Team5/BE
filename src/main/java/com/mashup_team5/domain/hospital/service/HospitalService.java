package com.mashup_team5.domain.hospital.service;

import com.mashup_team5.domain.hospital.dto.*;
import com.mashup_team5.domain.hospital.dto.HospitalDetailResponse.TreatmentDetailResponse;
import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.hospital.enums.HospitalBadgeType;
import com.mashup_team5.domain.hospital.enums.HospitalSort;
import com.mashup_team5.domain.hospital.enums.PriceType;
import com.mashup_team5.domain.hospital.repository.HospitalIdSlice;
import com.mashup_team5.domain.hospital.repository.HospitalRepository;
import com.mashup_team5.domain.hospital.repository.HospitalSearchCondition;
import com.mashup_team5.domain.hospital.repository.HospitalTreatmentRepository;
import com.mashup_team5.domain.hospital.repository.projection.HospitalTreatmentPriceRow;
import com.mashup_team5.domain.hospital.repository.projection.MallPriceRow;
import com.mashup_team5.domain.mall.repository.MallPriceRepository;
import com.mashup_team5.domain.mall.repository.MallRepository;
import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.region.repository.RegionRepository;
import com.mashup_team5.domain.review.policy.ReviewTreatmentMapping;
import com.mashup_team5.domain.review.repository.ReviewRepository;
import com.mashup_team5.domain.treatment.entity.Treatment;
import com.mashup_team5.domain.treatment.entity.TreatmentCategory;
import com.mashup_team5.domain.treatment.repository.MedicalCategoryRepository;
import com.mashup_team5.domain.treatment.repository.TreatmentCategoryRepository;
import com.mashup_team5.domain.treatment.repository.TreatmentRepository;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {

	private final HospitalRepository hospitalRepository;
	private final HospitalTreatmentRepository hospitalTreatmentRepository;
	private final MallPriceRepository mallPriceRepository;
	private final ReviewRepository reviewRepository;
	private final MedicalCategoryRepository medicalCategoryRepository;
	private final TreatmentCategoryRepository treatmentCategoryRepository;
	private final TreatmentRepository treatmentRepository;
	private final RegionRepository regionRepository;
	private final MallRepository mallRepository;

	public HospitalListResponse getHospitals(HospitalSearchRequest request) {
		HospitalSearchCondition condition = validatedCondition(request.toCondition());
		HospitalIdSlice slice = hospitalRepository.findHospitalIds(condition);
		if (slice.hospitalIds().isEmpty()) {
			return new HospitalListResponse(List.of(), condition.page(), condition.size(), false);
		}

		Map<Long, Hospital> hospitals = hospitalRepository.findAllWithRegionByIdIn(slice.hospitalIds())
				.stream()
				.collect(Collectors.toMap(Hospital::getId, Function.identity()));
		List<HospitalTreatmentPriceRow> priceRows = hospitalTreatmentRepository.findPriceRows(
				slice.hospitalIds(), condition.medicalCategoryId(), condition.treatmentCategoryId(),
				condition.treatmentIds(), !condition.treatmentIds().isEmpty());
		Map<Long, List<HospitalTreatmentPriceRow>> priceRowsByHospital = priceRows.stream()
				.collect(Collectors.groupingBy(HospitalTreatmentPriceRow::hospitalId));
		Map<Long, Integer> mallPrices = findMallPrices(condition.mallId(), priceRows);

		List<HospitalResponse> content = slice.hospitalIds().stream()
				.map(hospitalId -> toResponse(
						hospitals.get(hospitalId),
						priceRowsByHospital.getOrDefault(hospitalId, List.of()),
						mallPrices,
						condition))
				.toList();
		return new HospitalListResponse(content, condition.page(), condition.size(), slice.hasNext());
	}

	public long countHospitals(HospitalCountRequest request) {
		return hospitalRepository.countHospitals(validatedCondition(request.toCondition()));
	}

	public HospitalDetailResponse getHospitalDetail(Long hospitalId, Long treatmentId, Long mallId) {
		validateIds(hospitalId, treatmentId);
		validateMall(mallId);
		Hospital hospital = hospitalRepository.findAllWithRegionByIdIn(List.of(hospitalId)).stream()
				.findFirst()
				.orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_TREATMENT_NOT_FOUND));
		HospitalTreatmentPriceRow priceRow = hospitalTreatmentRepository
				.findPriceRows(List.of(hospitalId), null, null, List.of(treatmentId), true).stream()
				.findFirst()
				.orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_TREATMENT_NOT_FOUND));
		long totalReviewCount = reviewCount(hospital);
		long relatedReviewCount = reviewRepository.countMatchingReviews(
				hospitalId, ReviewTreatmentMapping.treatmentIdsFor(treatmentId), true);
		long otherReviewCount = Math.max(totalReviewCount - relatedReviewCount, 0L);
		Integer discountPrice = findMallPrices(mallId, List.of(priceRow)).get(priceRow.hospitalTreatmentId());
		return new HospitalDetailResponse(
				hospital.getId(),
				hospital.getName(),
				hospital.getAddress(),
				toRegionResponse(hospital.getRegion()),
				rating(hospital),
				totalReviewCount,
				relatedReviewCount,
				otherReviewCount,
				new TreatmentDetailResponse(
						priceRow.treatmentId(),
						priceRow.treatmentName(),
						priceRow.regularPrice(),
						discountPrice)
		);
	}

	private HospitalSearchCondition validatedCondition(HospitalSearchCondition condition) {
		if (condition.minPrice() != null && condition.maxPrice() != null
				&& condition.minPrice() > condition.maxPrice()) {
			throw new CustomException(ErrorCode.INVALID_PRICE_RANGE);
		}
		if (condition.sort() == HospitalSort.LOW_PRICE && condition.treatmentIds().isEmpty()) {
			throw new CustomException(ErrorCode.SORT_REQUIRES_TREATMENT);
		}
		validateCategories(condition);
		if (!condition.regionIds().isEmpty()
				&& regionRepository.countByIdIn(condition.regionIds()) != condition.regionIds().size()) {
			throw new CustomException(ErrorCode.REGION_NOT_FOUND);
		}
		validateMall(condition.mallId());
		return condition;
	}

	private void validateCategories(HospitalSearchCondition condition) {
		if (condition.medicalCategoryId() != null
				&& !medicalCategoryRepository.existsById(condition.medicalCategoryId())) {
			throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
		}
		if (condition.treatmentCategoryId() != null) {
			TreatmentCategory category = treatmentCategoryRepository.findById(condition.treatmentCategoryId())
					.orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
			if (condition.medicalCategoryId() != null && (category.getMedicalCategory() == null
					|| !Objects.equals(category.getMedicalCategory().getId(), condition.medicalCategoryId()))) {
				throw new CustomException(ErrorCode.CATEGORY_MISMATCH);
			}
		}
		if (condition.treatmentIds().isEmpty()) {
			return;
		}
		List<Treatment> treatments = treatmentRepository.findAllWithCategoriesByIdIn(condition.treatmentIds());
		if (treatments.size() != condition.treatmentIds().size()) {
			throw new CustomException(ErrorCode.INVALID_FILTER);
		}
		for (Treatment treatment : treatments) {
			TreatmentCategory category = treatment.getTreatmentCategory();
			if (condition.treatmentCategoryId() != null && (category == null
					|| !Objects.equals(category.getId(), condition.treatmentCategoryId()))) {
				throw new CustomException(ErrorCode.CATEGORY_MISMATCH);
			}
			if (condition.medicalCategoryId() != null && (category == null || category.getMedicalCategory() == null
					|| !Objects.equals(category.getMedicalCategory().getId(), condition.medicalCategoryId()))) {
				throw new CustomException(ErrorCode.CATEGORY_MISMATCH);
			}
		}
	}

	private void validateMall(Long mallId) {
		if (mallId != null && !mallRepository.existsById(mallId)) {
			throw new CustomException(ErrorCode.MALL_NOT_FOUND);
		}
	}

	private Map<Long, Integer> findMallPrices(
			Long mallId,
			List<HospitalTreatmentPriceRow> priceRows) {
		if (mallId == null || priceRows.isEmpty()) {
			return Map.of();
		}
		List<Long> hospitalTreatmentIds = priceRows.stream()
				.map(HospitalTreatmentPriceRow::hospitalTreatmentId)
				.toList();
		return mallPriceRepository.findPriceRows(mallId, hospitalTreatmentIds).stream()
				.collect(Collectors.toMap(MallPriceRow::hospitalTreatmentId, MallPriceRow::price));
	}

	private HospitalResponse toResponse(
			Hospital hospital,
			List<HospitalTreatmentPriceRow> priceRows,
			Map<Long, Integer> mallPrices,
			HospitalSearchCondition condition) {
		return new HospitalResponse(
				hospital.getId(),
				hospital.getName(),
				hospital.getAddress(),
				toRegionResponse(hospital.getRegion()),
				rating(hospital),
				reviewCount(hospital),
				hospital.getHasSpecialist(),
				hospital.getHasPublicPrice(),
				hospital.getHasNightClinic(),
				hospital.getHasHolidayClinic(),
				badgeTypes(hospital),
				priceRows.stream()
						.map(row -> toPriceResponse(row, mallPrices))
						.filter(price -> isInPriceRange(price.displayPrice(), condition))
						.toList()
		);
	}

	private double rating(Hospital hospital) {
		return hospital.getRating() == null ? 0.0 : hospital.getRating().doubleValue();
	}

	private long reviewCount(Hospital hospital) {
		return hospital.getReviewCount() == null ? 0L : hospital.getReviewCount();
	}

	private List<HospitalBadgeType> badgeTypes(Hospital hospital) {
		List<HospitalBadgeType> badges = new ArrayList<>();
		if (Boolean.TRUE.equals(hospital.getIsPopular())) {
			badges.add(HospitalBadgeType.POPULAR);
		}
		if (Boolean.TRUE.equals(hospital.getHasNightClinic())) {
			badges.add(HospitalBadgeType.NIGHT_CLINIC);
		}
		if (Boolean.TRUE.equals(hospital.getIsReservable())) {
			badges.add(HospitalBadgeType.SAME_DAY_BOOKING);
		}
		if (Boolean.TRUE.equals(hospital.getIsOpenToday())) {
			badges.add(HospitalBadgeType.OPEN_TODAY);
		}
		if (Boolean.TRUE.equals(hospital.getHasSpecialOffer())) {
			badges.add(HospitalBadgeType.SPECIAL_OFFER);
		}
		return List.copyOf(badges);
	}

	private void validateIds(Long hospitalId, Long treatmentId) {
		if (hospitalId == null || treatmentId == null || hospitalId < 1 || treatmentId < 1) {
			throw new CustomException(ErrorCode.INVALID_ID);
		}
	}

	private boolean isInPriceRange(Integer price, HospitalSearchCondition condition) {
		if (condition.minPrice() == null && condition.maxPrice() == null) {
			return true;
		}
		if (price == null) {
			return false;
		}
		return (condition.minPrice() == null || price >= condition.minPrice())
				&& (condition.maxPrice() == null || price <= condition.maxPrice());
	}

	private HospitalRegionResponse toRegionResponse(Region region) {
		return switch (region.getRegionType()) {
			case SIDO -> new HospitalRegionResponse(
					region.getId(), region.getName(), null, null, null, null);
			case SIGUNGU -> {
				Region sido = region.getParentRegion();
				yield new HospitalRegionResponse(
						sido.getId(), sido.getName(), region.getId(), region.getName(), null, null);
			}
			case DONG -> {
				Region sigungu = region.getParentRegion();
				Region sido = sigungu.getParentRegion();
				yield new HospitalRegionResponse(
						sido.getId(), sido.getName(),
						sigungu.getId(), sigungu.getName(),
						region.getId(), region.getName());
			}
		};
	}

	private TreatmentPriceResponse toPriceResponse(
			HospitalTreatmentPriceRow row,
			Map<Long, Integer> mallPrices) {
		Integer discountPrice = mallPrices.get(row.hospitalTreatmentId());
		return new TreatmentPriceResponse(
				row.hospitalTreatmentId(),
				row.treatmentId(),
				row.treatmentName(),
				row.regularPrice(),
				discountPrice,
				discountPrice == null ? row.regularPrice() : discountPrice,
				discountPrice == null ? PriceType.REGULAR : PriceType.MALL
		);
	}
}
