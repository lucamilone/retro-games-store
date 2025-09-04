package com.betacom.retrogames.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TipoMetodoPagamentoDTO {

	private Integer id;
	private String nome;
}