package com.betacom.retrogames.model;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "piattaforma")
public class Piattaforma {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 20, nullable = false, unique = true)
	private String codice;

	@Column(length = 150, nullable = false, unique = true)
	private String nome;

	@Column(name = "anno_uscita_piattaforma", nullable = false)
	@Check(constraints = "anno_uscita_piattaforma >= 1970 AND anno_uscita_piattaforma <= 2006")
	private Integer annoUscitaPiattaforma;

	@ManyToMany(mappedBy = "piattaforme", fetch = FetchType.LAZY)
	private Set<Prodotto> prodotti = new HashSet<>();
}
