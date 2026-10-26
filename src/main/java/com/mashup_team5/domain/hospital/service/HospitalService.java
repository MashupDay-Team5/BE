package com.mashup_team5.domain.hospital.service;

import com.mashup_team5.domain.hospital.dto.*;
import com.mashup_team5.domain.hospital.dto.HospitalDetailResponse.TreatmentDetailResponse;
import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.hospital.enums.HospitalSort;
import com.mashup_team5.domain.hospital.enums.PriceType;
import com.mashup_team5.domain.hospital.repository.HospitalIdSlice;
import com.mashup_team5.domain.hospital.repository.HospitalRepository;
import com.mashup_team5.domain.hospital.repository.HospitalSearchCondition;
import com.mashup_team5.domain.hospital.repository.HospitalTreatmentRepository;
import com.mashup_team5.domain.hospital.repository.projection.HospitalReviewStatistics;
import com.mashup_team5.domain.hospital.repository.projection.HospitalTreatmentPriceRow;
import com.mashup_team5.domain.hospital.repository.projection.MallPriceRow;
import com.mashup_team5.domain.mall.repository.MallPriceRepository;
import com.mashup_team5.domain.region.entity.Region;
import com.mashup_team5.domain.review.repository.ReviewRepository;
import com.mashup_team5.global.exception.CustomException;
import com.mashup_team5.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

	public HospitalListResponse getHospitals(HospitalSearchRequest request) {
		HospitalSearchCondition condition = validatedCondition(request);
		HospitalIdSlice slice = hospitalRepository.findHospitalIds(condition);
		if (slice.hospitalIds().isEmpty()) {
			return new HospitalListResponse(List.of(), condition.page(), condition.size(), false);
		}

		Map<Long, Hospital> hospitals = hospitalRepository.findAllWithRegionByIdIn(slice.hospitalIds())
				.stream()
				.collect(Collectors.toMap(Hospital::getId, Function.identity()));
		Map<Long, HospitalReviewStatistics> statistics = reviewRepository
				.findStatisticsByHospitalIds(slice.hospitalIds())
				.stream()
				.collect(Collectors.toMap(HospitalReviewStatistics::hospitalId, Function.identity()));
		List<HospitalTreatmentPriceRow> priceRows = hospitalTreatmentRepository.findPriceRows(
				slice.hospitalIds(), condition.treatmentCategoryId(), condition.treatmentId());
		Map<Long, List<HospitalTreatmentPriceRow>> priceRowsByHospital = priceRows.stream()
				.collect(Collectors.groupingBy(HospitalTreatmentPriceRow::hospitalId));
		Map<Long, Integer> mallPrices = findMallPrices(condition.mallId(), priceRows);

		List<HospitalResponse> content = slice.hospitalIds().stream()
				.map(hospitalId -> toResponse(
						hospitals.get(hospitalId),
						statistics.get(hospitalId),
						priceRowsByHospital.getOrDefault(hospitalId, List.of()),
						mallPrices,
						condition))
				.toList();
		return new HospitalListResponse(content, condition.page(), condition.size(), slice.hasNext());
	}

	public long countHospitals(HospitalSearchRequest request) {
		return hospitalRepository.countHospitals(validatedCondition(request));
	}

	public HospitalDetailResponse getHospitalDetail(Long hospitalId, Long treatmentId, Long mallId) {
		validateIds(hospitalId, treatmentId);
		Hospital hospital = hospitalRepository.findAllWithRegionByIdIn(List.of(hospitalId)).stream()
				.findFirst()
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
		HospitalTreatmentPriceRow priceRow = hospitalTreatmentRepository
				.findPriceRows(List.of(hospitalId), null, treatmentId).stream()
				.findFirst()
				.orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_TREATMENT_NOT_FOUND));
		HospitalReviewStatistics statistics = reviewRepository.findStatisticsByHospitalIds(List.of(hospitalId)).stream()
				.findFirst()
				.orElse(null);
		long totalReviewCount = reviewCount(statistics);
		long relatedReviewCount = reviewRepository.countMatchingReviews(hospitalId, treatmentId, true);
		long otherReviewCount = totalReviewCount - relatedReviewCount;
		Integer discountPrice = findMallPrices(mallId, List.of(priceRow)).get(priceRow.hospitalTreatmentId());
		return new HospitalDetailResponse(
				hospital.getId(),
				hospital.getName(),
				toRegionResponse(hospital.getRegion()),
				rating(statistics),
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

	private HospitalSearchCondition validatedCondition(HospitalSearchRequest request) {
		HospitalSearchCondition condition = request.toCondition();
		if (condition.minPrice() != null && condition.maxPrice() != null
				&& condition.minPrice() > condition.maxPrice()) {
			throw new CustomException(ErrorCode.INVALID_PRICE_RANGE);
		}
		if (condition.sort() == HospitalSort.LOW_PRICE && condition.treatmentId() == null) {
			throw new CustomException(ErrorCode.TREATMENT_REQUIRED_FOR_PRICE_SORT);
		}
		return condition;
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
			HospitalReviewStatistics statistics,
			List<HospitalTreatmentPriceRow> priceRows,
			Map<Long, Integer> mallPrices,
			HospitalSearchCondition condition) {
		return new HospitalResponse(
				hospital.getId(),
				hospital.getName(),
				toRegionResponse(hospital.getRegion()),
				rating(statistics),
				reviewCount(statistics),
				hospital.getHasSpecialist(),
				hospital.getHasPublicPrice(),
				hospital.getHasNightClinic(),
				hospital.getHasHolidayClinic(),
				hospital.getIsReservable(),
				priceRows.stream()
						.map(row -> toPriceResponse(row, mallPrices))
						.filter(price -> isInPriceRange(price.displayPrice(), condition))
						.toList()
		);
	}

	private double rating(HospitalReviewStatistics statistics) {
		return statistics == null || statistics.rating() == null
				? 0.0
				: Math.round(statistics.rating() * 10) / 10.0;
	}

	private long reviewCount(HospitalReviewStatistics statistics) {
		return statistics == null ? 0L : statistics.reviewCount();
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
				discountPrice == null ? PriceType.REGULAR : PriceType.MALL,
				row.isSpecialOffer()
		);
	}
}
