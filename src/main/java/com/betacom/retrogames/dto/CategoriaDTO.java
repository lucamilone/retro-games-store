package com.betacom.retrogames.dto;

import java.util.Set;

import com.betacom.retrogames.model.Prodotto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CategoriaDTO {

	private Integer id;
	private String nome;
	private Set<Prodotto> prodotti;
}