package com.betacom.retrogames.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PiattaformaDTO {

	private Integer id;
	private String codice;
	private String nome;
	private Integer annoUscitaPiattaforma;
}