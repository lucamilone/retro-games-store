package com.betacom.retrogames.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PagamentoReq {

	private Integer id;
	private Integer ordineId;
	private BigDecimal totale;
	private Integer metodoPagamentoId;
	private String statoPagamento;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}