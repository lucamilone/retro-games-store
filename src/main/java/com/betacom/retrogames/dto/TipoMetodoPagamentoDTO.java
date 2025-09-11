package com.betacom.retrogames.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TipoMetodoPagamentoDTO {

	private Integer id;
	private String nome;
	private Boolean attivo;
}