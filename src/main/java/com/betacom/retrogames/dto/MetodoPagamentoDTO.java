package com.betacom.retrogames.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MetodoPagamentoDTO {

	private Integer id;
	private Integer accountId;
	private TipoMetodoPagamentoDTO tipoMetodoPagamento;
	private String token;
	private boolean metodoDefault;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}