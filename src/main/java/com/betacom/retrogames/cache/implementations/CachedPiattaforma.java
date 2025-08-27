package com.betacom.retrogames.cache.implementations;

import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.model.Piattaforma;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CachedPiattaforma implements CacheableEntry {

	private Integer id;
	private String codice;
	private String nome;
	private Integer annoUscitaPiattaforma;

	public CachedPiattaforma(Piattaforma piattaforma) {
		this.id = piattaforma.getId();
		this.codice = piattaforma.getCodice();
		this.nome = piattaforma.getNome();
		this.annoUscitaPiattaforma = piattaforma.getAnnoUscitaPiattaforma();
	}
}