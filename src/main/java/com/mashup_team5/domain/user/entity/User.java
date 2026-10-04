package com.mashup_team5.domain.user.entity;

import com.mashup_team5.domain.mall.entity.Mall;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mall_id")
	private Mall mall;

	@Column(name = "email")
	private String email;

	@Column(name = "name")
	private String name;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Builder
	public User(Mall mall, String email, String name, String nickname, String phoneNumber) {
		this.mall = mall;
		this.email = email;
		this.name = name;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
	}
}
