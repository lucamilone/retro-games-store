package com.betacom.retrogames.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "credenziale")
public class Credenziale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@Column(length = 255, nullable = false, unique = true)
	private String email;

	@Column(length = 255, nullable = false)
	private String password;

	@Column(name = "ultimo_login")
	private LocalDateTime ultimoLogin;

	@Column(nullable = false)
	private boolean attivo;
}
