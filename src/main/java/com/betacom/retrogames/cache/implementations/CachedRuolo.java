package com.betacom.retrogames.cache.implementations;

import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.model.Ruolo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CachedRuolo implements CacheableEntry {

	private Integer id;
	private String nome;

	public CachedRuolo(Ruolo ruolo) {
		this.id = ruolo.getId();
		this.nome = ruolo.getNome();
	}
}
