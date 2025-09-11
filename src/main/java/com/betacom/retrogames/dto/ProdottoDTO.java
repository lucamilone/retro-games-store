package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProdottoDTO {

	private Integer id;
	private String sku;
	private String nome;
	private String categoria;
	private List<PiattaformaDTO> piattaforme;
	private String descrizione;
	private Integer annoUscita;
	private BigDecimal prezzo;
	private String imgUrl;
	private Boolean attivo;
}