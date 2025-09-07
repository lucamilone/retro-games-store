package com.betacom.retrogames.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PagamentoDTO {

	private Integer id;
	private Integer ordineId;
	private BigDecimal totale;
	private MetodoPagamentoDTO metodoPagamento;
	private String statoPagamento;
}