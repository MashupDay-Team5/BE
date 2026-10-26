package com.mashup_team5.domain.hospital.repository;

import com.mashup_team5.domain.hospital.dto.HospitalSort;
import com.mashup_team5.domain.hospital.entity.Hospital;
import com.mashup_team5.domain.hospital.entity.HospitalTreatment;
import com.mashup_team5.domain.mall.entity.MallPrice;
import com.mashup_team5.domain.review.entity.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HospitalSearchRepositoryImpl implements HospitalSearchRepository {

	private final EntityManager entityManager;

	@Override
	public HospitalIdSlice findHospitalIds(HospitalSearchCondition condition) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Hospital> hospital = query.from(Hospital.class);
		List<Predicate> predicates = HospitalSearchPredicateBuilder.build(
			criteriaBuilder, query, hospital, condition);

		query.select(hospital.get("id"))
			.where(predicates.toArray(Predicate[]::new))
			.orderBy(orderBy(criteriaBuilder, query, hospital, condition));

		TypedQuery<Long> typedQuery = entityManager.createQuery(query)
			.setFirstResult(condition.page() * condition.size())
			.setMaxResults(condition.size() + 1);
		List<Long> result = new ArrayList<>(typedQuery.getResultList());
		boolean hasNext = result.size() > condition.size();
		if (hasNext) {
			result.remove(result.size() - 1);
		}
		return new HospitalIdSlice(List.copyOf(result), hasNext);
	}

	@Override
	public long countHospitals(HospitalSearchCondition condition) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
		Root<Hospital> hospital = query.from(Hospital.class);
		List<Predicate> predicates = HospitalSearchPredicateBuilder.build(
			criteriaBuilder, query, hospital, condition);
		query.select(criteriaBuilder.countDistinct(hospital.get("id")))
			.where(predicates.toArray(Predicate[]::new));
		return entityManager.createQuery(query).getSingleResult();
	}

	private List<Order> orderBy(
		CriteriaBuilder criteriaBuilder,
		CriteriaQuery<Long> query,
		Root<Hospital> hospital,
		HospitalSearchCondition condition) {
		List<Order> orders = new ArrayList<>();
		HospitalSort sort = condition.sort();
		if (sort == HospitalSort.RATING) {
			orders.add(criteriaBuilder.desc(criteriaBuilder.coalesce(
				reviewRating(query, criteriaBuilder, hospital), 0.0)));
		} else if (sort == HospitalSort.REVIEW_COUNT) {
			orders.add(criteriaBuilder.desc(reviewCount(query, criteriaBuilder, hospital)));
		} else if (sort == HospitalSort.LOW_PRICE) {
			orders.add(criteriaBuilder.asc(criteriaBuilder.coalesce(
				effectivePrice(query, criteriaBuilder, hospital, condition), Integer.MAX_VALUE)));
		} else {
			orders.add(criteriaBuilder.desc(criteriaBuilder.coalesce(
				hospital.get("monthlyVisitCount"), 0)));
		}
		orders.add(criteriaBuilder.asc(hospital.get("id")));
		return orders;
	}

	private Subquery<Double> reviewRating(
		CriteriaQuery<Long> query,
		CriteriaBuilder criteriaBuilder,
		Root<Hospital> hospital) {
		Subquery<Double> subquery = query.subquery(Double.class);
		Root<Review> review = subquery.from(Review.class);
		subquery.select(criteriaBuilder.avg(review.<BigDecimal>get("rating")))
			.where(criteriaBuilder.equal(review.get("hospital"), hospital));
		return subquery;
	}

	private Subquery<Long> reviewCount(
		CriteriaQuery<Long> query,
		CriteriaBuilder criteriaBuilder,
		Root<Hospital> hospital) {
		Subquery<Long> subquery = query.subquery(Long.class);
		Root<Review> review = subquery.from(Review.class);
		subquery.select(criteriaBuilder.count(review.get("id")))
			.where(criteriaBuilder.equal(review.get("hospital"), hospital));
		return subquery;
	}

	private Expression<Integer> effectivePrice(
		CriteriaQuery<Long> query,
		CriteriaBuilder criteriaBuilder,
		Root<Hospital> hospital,
		HospitalSearchCondition condition) {
		Subquery<Integer> regularPrice = query.subquery(Integer.class);
		Root<HospitalTreatment> hospitalTreatment = regularPrice.from(HospitalTreatment.class);
		regularPrice.select(hospitalTreatment.get("regularPrice"))
			.where(
				criteriaBuilder.equal(hospitalTreatment.get("hospital"), hospital),
				criteriaBuilder.equal(
					hospitalTreatment.get("treatment").get("id"), condition.treatmentId())
			);
		if (condition.mallId() == null) {
			return regularPrice;
		}

		Subquery<Integer> mallPrice = query.subquery(Integer.class);
		Root<MallPrice> price = mallPrice.from(MallPrice.class);
		mallPrice.select(price.get("price"))
			.where(
				criteriaBuilder.equal(price.get("hospitalTreatment").get("hospital"), hospital),
				criteriaBuilder.equal(
					price.get("hospitalTreatment").get("treatment").get("id"), condition.treatmentId()),
				criteriaBuilder.equal(price.get("mall").get("id"), condition.mallId())
			);
		return criteriaBuilder.coalesce(mallPrice, regularPrice);
	}
}
