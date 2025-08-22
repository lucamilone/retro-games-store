package com.betacom.retrogames.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

	@Column(length = 50, nullable = false, unique = true)
	private String sku;

	@Column(length = 200, nullable = false)
	private String nome;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id", nullable = false)
	private Categoria categoria;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "prodotto_piattaforma", joinColumns = @JoinColumn(name = "prodotto_id"), inverseJoinColumns = @JoinColumn(name = "piattaforma_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"prodotto_id", "piattaforma_id" }))
	private Set<Piattaforma> piattaforme = new HashSet<>();

	@Column(columnDefinition = "TEXT", nullable = false)
	private String descrizione;

	@Column(name = "anno_uscita")
	@Check(constraints = "anno_uscita >= 1970 AND anno_uscita <= EXTRACT(YEAR FROM CURRENT_DATE)")
	private Integer annoUscita;

	@Column(precision = 10, scale = 2, nullable = false)
	@Check(constraints = "prezzo > 0")
	private BigDecimal prezzo;
}
