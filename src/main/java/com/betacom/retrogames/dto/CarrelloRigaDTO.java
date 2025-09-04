package com.betacom.retrogames.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CarrelloRigaDTO {

	private Integer id;
	private ProdottoDTO prodotto;
	private Integer quantita;

	// Campi aggiuntivi utili al frontend
	private BigDecimal prezzoUnitario; // Prezzo attuale del prodotto
	private BigDecimal totale; // prezzoUnitario * quantita
	private String sku; // Codice prodotto
	private String immagineUrl; // Per la visualizzazione del prodotto
}