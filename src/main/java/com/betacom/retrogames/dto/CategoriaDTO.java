package com.betacom.retrogames.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CategoriaDTO {

	private Integer id;
	private String nome;
	private Set<ProdottoDTO> prodotti;
}