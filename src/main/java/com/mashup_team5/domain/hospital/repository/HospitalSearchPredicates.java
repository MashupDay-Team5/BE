package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.entity.QHospital;
import com.mashup_team5.domain.hospital.entity.QHospitalTreatment;
import com.mashup_team5.domain.mall.entity.QMallPrice;
import com.mashup_team5.domain.region.entity.QRegion;
import com.mashup_team5.domain.treatment.entity.QTreatment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;

final class HospitalSearchPredicates {

	private HospitalSearchPredicates() {
	}

	static BooleanExpression treatmentCondition(
		QHospital hospital,
		HospitalSearchCondition condition) {
		if (hasPriceRange(condition)
			|| condition.treatmentId() == null && condition.treatmentCategoryId() == null) {
			return null;
		}
		QHospitalTreatment hospitalTreatment = new QHospitalTreatment("treatmentFilter");
		QTreatment treatment = new QTreatment("targetTreatment");
		return JPAExpressions.selectOne()
			.from(hospitalTreatment)
			.join(hospitalTreatment.treatment, treatment)
			.where(
				hospitalTreatment.hospital.eq(hospital),
				treatmentTarget(treatment, condition)
			)
			.exists();
	}

	static BooleanExpression regionCondition(
		HospitalSearchCondition condition,
		QRegion region,
		QRegion parentRegion,
		QRegion grandParentRegion) {
		if (condition.regionIds().isEmpty()) {
			return null;
		}
		return region.id.in(condition.regionIds())
			.or(parentRegion.id.in(condition.regionIds()))
			.or(grandParentRegion.id.in(condition.regionIds()));
	}

	static BooleanExpression specialistCondition(QHospital hospital, HospitalSearchCondition condition) {
		return Boolean.TRUE.equals(condition.hasSpecialist()) ? hospital.hasSpecialist.isTrue() : null;
	}

	static BooleanExpression publicPriceCondition(QHospital hospital, HospitalSearchCondition condition) {
		return Boolean.TRUE.equals(condition.hasPublicPrice()) ? hospital.hasPublicPrice.isTrue() : null;
	}

	static BooleanExpression nightClinicCondition(QHospital hospital, HospitalSearchCondition condition) {
		return Boolean.TRUE.equals(condition.hasNightClinic()) ? hospital.hasNightClinic.isTrue() : null;
	}

	static BooleanExpression holidayClinicCondition(QHospital hospital, HospitalSearchCondition condition) {
		return Boolean.TRUE.equals(condition.hasHolidayClinic()) ? hospital.hasHolidayClinic.isTrue() : null;
	}

	static BooleanExpression priceCondition(QHospital hospital, HospitalSearchCondition condition) {
		if (!hasPriceRange(condition)) {
			return null;
		}
		BooleanExpression regularPriceMatches = regularPriceCondition(hospital, condition);
		if (condition.mallId() == null) {
			return regularPriceMatches;
		}
		return mallPriceCondition(hospital, condition).or(regularPriceMatches);
	}

	private static BooleanExpression mallPriceCondition(
		QHospital hospital,
		HospitalSearchCondition condition) {
		QMallPrice mallPrice = new QMallPrice("mallPriceFilter");
		QHospitalTreatment hospitalTreatment = new QHospitalTreatment("mallHospitalTreatment");
		QTreatment treatment = new QTreatment("mallTreatment");
		return JPAExpressions.selectOne()
			.from(mallPrice)
			.join(mallPrice.hospitalTreatment, hospitalTreatment)
			.join(hospitalTreatment.treatment, treatment)
			.where(
				hospitalTreatment.hospital.eq(hospital),
				mallPrice.mall.id.eq(condition.mallId()),
				treatmentTarget(treatment, condition),
				priceRange(mallPrice.price, condition)
			)
			.exists();
	}

	private static BooleanExpression regularPriceCondition(
		QHospital hospital,
		HospitalSearchCondition condition) {
		QHospitalTreatment hospitalTreatment = new QHospitalTreatment("regularPriceFilter");
		QTreatment treatment = new QTreatment("regularPriceTreatment");
		return JPAExpressions.selectOne()
			.from(hospitalTreatment)
			.join(hospitalTreatment.treatment, treatment)
			.where(
				hospitalTreatment.hospital.eq(hospital),
				treatmentTarget(treatment, condition),
				priceRange(hospitalTreatment.regularPrice, condition),
				mallPriceAbsent(hospitalTreatment, condition.mallId())
			)
			.exists();
	}

	private static BooleanExpression mallPriceAbsent(
		QHospitalTreatment hospitalTreatment,
		Long mallId) {
		if (mallId == null) {
			return null;
		}
		QMallPrice mallPrice = new QMallPrice("fallbackMallPrice");
		return JPAExpressions.selectOne()
			.from(mallPrice)
			.where(
				mallPrice.hospitalTreatment.eq(hospitalTreatment),
				mallPrice.mall.id.eq(mallId)
			)
			.notExists();
	}

	private static BooleanExpression treatmentTarget(
		QTreatment treatment,
		HospitalSearchCondition condition) {
		if (condition.treatmentId() == null && condition.treatmentCategoryId() == null) {
			return null;
		}
		if (condition.treatmentId() != null && condition.treatmentCategoryId() != null) {
			return treatment.id.eq(condition.treatmentId())
				.and(treatment.treatmentCategory.id.eq(condition.treatmentCategoryId()));
		}
		return condition.treatmentId() != null
			? treatment.id.eq(condition.treatmentId())
			: treatment.treatmentCategory.id.eq(condition.treatmentCategoryId());
	}

	private static BooleanExpression priceRange(
		NumberPath<Integer> price,
		HospitalSearchCondition condition) {
		if (condition.minPrice() != null && condition.maxPrice() != null) {
			return price.between(condition.minPrice(), condition.maxPrice());
		}
		return condition.minPrice() != null
			? price.goe(condition.minPrice())
			: price.loe(condition.maxPrice());
	}

	private static boolean hasPriceRange(HospitalSearchCondition condition) {
		return condition.minPrice() != null || condition.maxPrice() != null;
	}
}
