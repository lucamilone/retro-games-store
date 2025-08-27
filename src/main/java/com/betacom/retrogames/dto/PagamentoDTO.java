package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PagamentoDTO {

	private Integer id;
	private String ordine;
	private BigDecimal totale;
	private String metodoPagamento;
	private String statoPagamento;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}