package com.betacom.retrogames.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrdineRigaDTO {

	private Integer id;
	private Integer ordineId;
	private ProdottoDTO prodotto;
	private Integer quantita;
	private BigDecimal prezzoUnitario;

	// Campi aggiuntivi utili al frontend
	private BigDecimal subTotale; // prezzoUnitario * quantita
}