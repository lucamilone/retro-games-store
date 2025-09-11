package com.betacom.retrogames.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PiattaformaDTO {

	private Integer id;
	private String codice;
	private String nome;
	private Integer annoUscitaPiattaforma;
	private List<ProdottoDTO> prodotti;
	private Boolean attivo;
}