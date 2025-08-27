package com.betacom.retrogames.cache.implementations;

import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.model.TipoMetodoPagamento;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CachedTipoMetodoPagamento implements CacheableEntry {

	private Integer id;
	private String nome;

	public CachedTipoMetodoPagamento(TipoMetodoPagamento tipoMetodoPagamento) {
		this.id = tipoMetodoPagamento.getId();
		this.nome = tipoMetodoPagamento.getNome();
	}
}