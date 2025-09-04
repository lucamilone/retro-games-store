package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PagamentoDTO {

	private Integer id;
	private OrdineDTO ordine;
	private BigDecimal totale;
	private MetodoPagamentoDTO metodoPagamento;
	private String statoPagamento;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}