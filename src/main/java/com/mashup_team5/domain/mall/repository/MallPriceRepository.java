package com.mashup_team5.domain.mall.repository;

import com.mashup_team5.domain.hospital.repository.projection.MallPriceRow;
import com.mashup_team5.domain.mall.entity.MallPrice;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MallPriceRepository extends JpaRepository<MallPrice, Long> {

	@Query("""
		select new com.mashup_team5.domain.hospital.repository.projection.MallPriceRow(
			mp.hospitalTreatment.id, mp.price
		)
		from MallPrice mp
		where mp.mall.id = :mallId
		  and mp.hospitalTreatment.id in :hospitalTreatmentIds
		""")
	List<MallPriceRow> findPriceRows(
		@Param("mallId") Long mallId,
		@Param("hospitalTreatmentIds") Collection<Long> hospitalTreatmentIds);
}
