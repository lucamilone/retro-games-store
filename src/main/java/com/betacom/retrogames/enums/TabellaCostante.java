package com.betacom.retrogames.enums;

public enum TabellaCostante {

	CATEGORIA("categoria"), PIATTAFORMA("piattaforma"), RUOLO("ruolo"), TIPO_METODO_PAGAMENTO("tipo_metodo_pagamento");

	private final String nomeTabella;

	private TabellaCostante(String nomeTabella) {
		this.nomeTabella = nomeTabella;
	}

	public String getNomeTabella() {
		return nomeTabella;
	}
}
