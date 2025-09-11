package com.betacom.retrogames.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MetodoPagamentoDTO {

	private Integer id;
	private Integer accountId;
	private TipoMetodoPagamentoDTO tipo;
	private String token;
	private Boolean metodoDefault;
	private Boolean attivo;
}