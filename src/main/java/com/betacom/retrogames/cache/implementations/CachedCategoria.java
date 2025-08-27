package com.betacom.retrogames.cache.implementations;

import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.model.Categoria;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CachedCategoria implements CacheableEntry {

	private Integer id;
	private String nome;

	public CachedCategoria(Categoria categoria) {
		this.id = categoria.getId();
		this.nome = categoria.getNome();
	}
}