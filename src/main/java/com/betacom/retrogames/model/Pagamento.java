package com.betacom.retrogames.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.betacom.retrogames.model.enums.MetodoPagamento;
import com.betacom.retrogames.model.enums.StatoPagamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "pagamento")
public class Pagamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ordine_id", nullable = false)
	private Ordine ordine;

	@Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal totale;

	@Enumerated(EnumType.STRING)
	@Column(name = "metodo_pagamento", length = 20, nullable = false)
	private MetodoPagamento metodoPagamento;

	@Enumerated(EnumType.STRING)
	@Column(name = "stato_pagamento", nullable = false)
	private StatoPagamento statoPagamento = StatoPagamento.IN_ATTESA;

	@CreationTimestamp
	@Column(name = "creato_il", nullable = false, updatable = false)
	private LocalDateTime creatoIl;
}
