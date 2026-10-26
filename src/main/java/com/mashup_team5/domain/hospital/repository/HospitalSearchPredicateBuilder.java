package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.mall.entity.MallPrice;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;

final class HospitalSearchPredicateBuilder {

	private HospitalSearchPredicateBuilder() {
	}

	static List<Predicate> build(
		CriteriaBuilder criteriaBuilder,
		AbstractQuery<?> query,
		Root<Hospital> hospital,
		HospitalSearchCondition condition) {
		List<Predicate> predicates = new ArrayList<>();

		if (!condition.regionIds().isEmpty()) {
			Join<Object, Object> region = hospital.join("region", JoinType.LEFT);
			Join<Object, Object> parentRegion = region.join("parentRegion", JoinType.LEFT);
			Join<Object, Object> grandParentRegion = parentRegion.join("parentRegion", JoinType.LEFT);
			predicates.add(criteriaBuilder.or(
				region.get("id").in(condition.regionIds()),
				parentRegion.get("id").in(condition.regionIds()),
				grandParentRegion.get("id").in(condition.regionIds())
			));
		}
		addBooleanPredicate(criteriaBuilder, predicates, hospital, "hasSpecialist", condition.hasSpecialist());
		addBooleanPredicate(criteriaBuilder, predicates, hospital, "hasPublicPrice", condition.hasPublicPrice());
		addBooleanPredicate(criteriaBuilder, predicates, hospital, "hasNightClinic", condition.hasNightClinic());
		addBooleanPredicate(criteriaBuilder, predicates, hospital, "hasHolidayClinic", condition.hasHolidayClinic());

		if (condition.minPrice() != null || condition.maxPrice() != null) {
			predicates.add(pricePredicate(criteriaBuilder, query, hospital, condition));
		} else if (condition.treatmentId() != null || condition.treatmentCategoryId() != null) {
			predicates.add(treatmentPredicate(criteriaBuilder, query, hospital, condition));
		}
		return predicates;
	}

	private static void addBooleanPredicate(
		CriteriaBuilder criteriaBuilder,
		List<Predicate> predicates,
		Root<Hospital> hospital,
		String field,
		Boolean selected) {
		if (Boolean.TRUE.equals(selected)) {
			predicates.add(criteriaBuilder.isTrue(hospital.get(field)));
		}
	}

	private static Predicate treatmentPredicate(
		CriteriaBuilder criteriaBuilder,
		AbstractQuery<?> query,
		Root<Hospital> hospital,
		HospitalSearchCondition condition) {
		Subquery<Long> subquery = query.subquery(Long.class);
		Root<HospitalTreatment> hospitalTreatment = subquery.from(HospitalTreatment.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(hospitalTreatment.get("hospital"), hospital));
		addTreatmentTarget(
			criteriaBuilder, predicates, hospitalTreatment.get("treatment"), condition);
		subquery.select(hospitalTreatment.get("id"))
			.where(predicates.toArray(Predicate[]::new));
		return criteriaBuilder.exists(subquery);
	}

	private static Predicate pricePredicate(
		CriteriaBuilder criteriaBuilder,
		AbstractQuery<?> query,
		Root<Hospital> hospital,
		HospitalSearchCondition condition) {
		Predicate regularPriceMatches = regularPricePredicate(criteriaBuilder, query, hospital, condition);
		if (condition.mallId() == null) {
			return regularPriceMatches;
		}
		return criteriaBuilder.or(
			mallPricePredicate(criteriaBuilder, query, hospital, condition),
			regularPriceMatches
		);
	}

	private static Predicate mallPricePredicate(
		CriteriaBuilder criteriaBuilder,
		AbstractQuery<?> query,
		Root<Hospital> hospital,
		HospitalSearchCondition condition) {
		Subquery<Long> subquery = query.subquery(Long.class);
		Root<MallPrice> mallPrice = subquery.from(MallPrice.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(
			mallPrice.get("hospitalTreatment").get("hospital"), hospital));
		predicates.add(criteriaBuilder.equal(mallPrice.get("mall").get("id"), condition.mallId()));
		addTreatmentTarget(
			criteriaBuilder,
			predicates,
			mallPrice.get("hospitalTreatment").get("treatment"),
			condition);
		addPriceRange(criteriaBuilder, predicates, mallPrice.get("price"), condition);
		subquery.select(mallPrice.get("id")).where(predicates.toArray(Predicate[]::new));
		return criteriaBuilder.exists(subquery);
	}

	private static Predicate regularPricePredicate(
		CriteriaBuilder criteriaBuilder,
		AbstractQuery<?> query,
		Root<Hospital> hospital,
		HospitalSearchCondition condition) {
		Subquery<Long> subquery = query.subquery(Long.class);
		Root<HospitalTreatment> hospitalTreatment = subquery.from(HospitalTreatment.class);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(hospitalTreatment.get("hospital"), hospital));
		addTreatmentTarget(
			criteriaBuilder, predicates, hospitalTreatment.get("treatment"), condition);
		addPriceRange(criteriaBuilder, predicates, hospitalTreatment.get("regularPrice"), condition);
		if (condition.mallId() != null) {
			Subquery<Long> mallPriceExists = subquery.subquery(Long.class);
			Root<MallPrice> mallPrice = mallPriceExists.from(MallPrice.class);
			mallPriceExists.select(mallPrice.get("id"))
				.where(
					criteriaBuilder.equal(mallPrice.get("hospitalTreatment"), hospitalTreatment),
					criteriaBuilder.equal(mallPrice.get("mall").get("id"), condition.mallId())
				);
			predicates.add(criteriaBuilder.not(criteriaBuilder.exists(mallPriceExists)));
		}
		subquery.select(hospitalTreatment.get("id")).where(predicates.toArray(Predicate[]::new));
		return criteriaBuilder.exists(subquery);
	}

	private static void addTreatmentTarget(
		CriteriaBuilder criteriaBuilder,
		List<Predicate> predicates,
		jakarta.persistence.criteria.Path<?> treatment,
		HospitalSearchCondition condition) {
		if (condition.treatmentId() != null) {
			predicates.add(criteriaBuilder.equal(treatment.get("id"), condition.treatmentId()));
		}
		if (condition.treatmentCategoryId() != null) {
			predicates.add(criteriaBuilder.equal(
				treatment.get("treatmentCategory").get("id"), condition.treatmentCategoryId()));
		}
	}

	private static void addPriceRange(
		CriteriaBuilder criteriaBuilder,
		List<Predicate> predicates,
		jakarta.persistence.criteria.Path<Integer> price,
		HospitalSearchCondition condition) {
		if (condition.minPrice() != null) {
			predicates.add(criteriaBuilder.greaterThanOrEqualTo(price, condition.minPrice()));
		}
		if (condition.maxPrice() != null) {
			predicates.add(criteriaBuilder.lessThanOrEqualTo(price, condition.maxPrice()));
		}
	}
}
