package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.util.Set;

import com.betacom.retrogames.model.Piattaforma;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ProdottoDTO {

	private Integer id;
	private String sku;
	private String nome;
	private String categoria;
	private Set<Piattaforma> piattaforme;
	private String descrizione;
	private Integer annoUscita;
	private BigDecimal prezzo;
}