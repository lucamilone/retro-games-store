package com.betacom.retrogames.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OrdineRigaDTO {

	private Integer id;
	private OrdineDTO ordine;
	private ProdottoDTO prodotto;
	private Integer quantita;
	private BigDecimal prezzoUnitario;
	private BigDecimal totale;
}