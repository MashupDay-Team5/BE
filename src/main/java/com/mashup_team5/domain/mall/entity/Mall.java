package com.mashup_team5.domain.mall.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "mall")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mall {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mall_id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Builder
	public Mall(String name) {
		this.name = name;
	}
}
