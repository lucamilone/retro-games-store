package com.betacom.retrogames.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CarrelloRigaDTO {

	private Integer id;
	private Integer carrelloId;
	private ProdottoDTO prodotto;
	private Integer quantita;

	// Campi aggiuntivi utili al frontend
	private BigDecimal subTotale; // prezzo del prodotto * quantita
}