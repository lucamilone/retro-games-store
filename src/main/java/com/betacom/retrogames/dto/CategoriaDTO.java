package com.betacom.retrogames.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoriaDTO {

	private Integer id;
	private String nome;
	private Set<ProdottoDTO> prodotti;
}