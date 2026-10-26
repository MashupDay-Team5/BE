package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.entity.QHospital;
import com.mashup_team5.domain.region.entity.QRegion;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HospitalSearchRepositoryImpl implements HospitalSearchRepository {

	private static final QHospital hospital = QHospital.hospital;
	private static final QRegion region = new QRegion("hospitalRegion");
	private static final QRegion parentRegion = new QRegion("parentRegion");
	private static final QRegion grandParentRegion = new QRegion("grandParentRegion");
	private final JPAQueryFactory queryFactory;

	@Override
	public HospitalIdSlice findHospitalIds(HospitalSearchCondition condition) {
		JPAQuery<Long> query = queryFactory.select(hospital.id).from(hospital);
		applyRegionJoins(query, condition);
		List<Long> result = new ArrayList<>(query
			.where(whereConditions(condition))
			.orderBy(HospitalSearchOrders.orderSpecifiers(hospital, condition))
			.offset((long)condition.page() * condition.size())
			.limit(condition.size() + 1L)
			.fetch());

		boolean hasNext = result.size() > condition.size();
		if (hasNext) {
			result.remove(result.size() - 1);
		}
		return new HospitalIdSlice(List.copyOf(result), hasNext);
	}

	@Override
	public long countHospitals(HospitalSearchCondition condition) {
		JPAQuery<Long> query = queryFactory.select(hospital.id.countDistinct()).from(hospital);
		applyRegionJoins(query, condition);
		Long count = query.where(whereConditions(condition)).fetchOne();
		return count == null ? 0L : count;
	}

	private Predicate[] whereConditions(HospitalSearchCondition condition) {
		return new Predicate[] {
			HospitalSearchPredicates.treatmentCondition(hospital, condition),
			HospitalSearchPredicates.regionCondition(condition, region, parentRegion, grandParentRegion),
			HospitalSearchPredicates.specialistCondition(hospital, condition),
			HospitalSearchPredicates.publicPriceCondition(hospital, condition),
			HospitalSearchPredicates.nightClinicCondition(hospital, condition),
			HospitalSearchPredicates.holidayClinicCondition(hospital, condition),
			HospitalSearchPredicates.priceCondition(hospital, condition)
		};
	}

	private void applyRegionJoins(JPAQuery<?> query, HospitalSearchCondition condition) {
		if (!condition.regionIds().isEmpty()) {
			query.leftJoin(hospital.region, region)
				.leftJoin(region.parentRegion, parentRegion)
				.leftJoin(parentRegion.parentRegion, grandParentRegion);
		}
	}
}
