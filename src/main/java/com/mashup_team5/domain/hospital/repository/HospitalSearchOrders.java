package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.entity.QHospital;
import com.mashup_team5.domain.hospital.entity.QHospitalTreatment;
import com.mashup_team5.domain.mall.entity.QMallPrice;
import com.mashup_team5.domain.treatment.entity.QTreatment;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLSubQuery;
import java.math.BigDecimal;

final class HospitalSearchOrders {

	private HospitalSearchOrders() {
	}

	static OrderSpecifier<?>[] orderSpecifiers(
		QHospital hospital,
		HospitalSearchCondition condition) {
		OrderSpecifier<?> primaryOrder = switch (condition.sort()) {
			case RATING -> hospital.rating.coalesce(BigDecimal.ZERO).desc();
			case REVIEW_COUNT -> hospital.reviewCount.coalesce(0).desc();
			case LOW_PRICE -> effectivePrice(hospital, condition).asc();
			case VISIT -> hospital.monthlyVisitCount.coalesce(0).desc();
		};
		return new OrderSpecifier<?>[] {primaryOrder, hospital.id.asc()};
	}

	private static NumberExpression<Integer> effectivePrice(
		QHospital hospital,
		HospitalSearchCondition condition) {
		QHospitalTreatment hospitalTreatment = new QHospitalTreatment("sortHospitalTreatment");
		QTreatment treatment = new QTreatment("sortTreatment");
		JPQLSubQuery<Integer> regularPrice = JPAExpressions.select(hospitalTreatment.regularPrice)
			.from(hospitalTreatment)
			.join(hospitalTreatment.treatment, treatment)
			.where(
				hospitalTreatment.hospital.eq(hospital),
				treatment.id.eq(condition.treatmentId())
			);
		if (condition.mallId() == null) {
			return Expressions.numberTemplate(
				Integer.class, "coalesce({0}, {1})", regularPrice, Integer.MAX_VALUE);
		}

		QMallPrice mallPrice = new QMallPrice("sortMallPrice");
		QHospitalTreatment mallHospitalTreatment = new QHospitalTreatment("sortMallHospitalTreatment");
		QTreatment mallTreatment = new QTreatment("sortMallTreatment");
		JPQLSubQuery<Integer> discountPrice = JPAExpressions.select(mallPrice.price)
			.from(mallPrice)
			.join(mallPrice.hospitalTreatment, mallHospitalTreatment)
			.join(mallHospitalTreatment.treatment, mallTreatment)
			.where(
				mallHospitalTreatment.hospital.eq(hospital),
				mallTreatment.id.eq(condition.treatmentId()),
				mallPrice.mall.id.eq(condition.mallId())
			);
		return Expressions.numberTemplate(
			Integer.class, "coalesce({0}, {1}, {2})", discountPrice, regularPrice, Integer.MAX_VALUE);
	}
}
