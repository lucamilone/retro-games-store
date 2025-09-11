package com.betacom.retrogames.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoriaDTO {

	private Integer id;
	private String nome;
	private List<ProdottoDTO> prodotti;
	private Boolean attivo;
}