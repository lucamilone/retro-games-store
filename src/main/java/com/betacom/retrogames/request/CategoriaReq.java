package com.betacom.retrogames.request;

import java.util.Set;

import com.betacom.retrogames.model.Prodotto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoriaReq {

	private Integer id;
	private String nome;
	private Set<Prodotto> prodottiId;
}