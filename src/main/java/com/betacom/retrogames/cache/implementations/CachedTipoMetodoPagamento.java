package com.betacom.retrogames.cache.implementations;

import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.model.TipoMetodoPagamento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CachedTipoMetodoPagamento implements CacheableEntry {

	private Integer id;
	private String nome;
	private Boolean attivo;

	public CachedTipoMetodoPagamento(TipoMetodoPagamento tipoMetodoPagamento) {
		this.id = tipoMetodoPagamento.getId();
		this.nome = tipoMetodoPagamento.getNome();
		this.attivo = tipoMetodoPagamento.getAttivo();
	}
}