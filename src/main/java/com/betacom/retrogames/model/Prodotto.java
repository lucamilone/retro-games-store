package com.betacom.retrogames.model;

import java.math.BigDecimal;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "prodotto")
public class Prodotto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false)
	private String nome;

	@Column(name = "categoria", nullable = false)
	private String categoria;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String descrizione;

	@Column(name = "anno_uscita")
	@Check(constraints = "anno_uscita >= 1970 AND anno_uscita <= EXTRACT(YEAR FROM CURRENT_DATE)")
	private Integer annoUscita;

	@Column(precision = 10, scale = 2, nullable = false)
	private BigDecimal prezzo;
}
