package com.betacom.retrogames.enums;

public enum Categoria {

	VIDEOGIOCHI("videogiochi"), CONSOLE("console"), ACCESSORI("accessori");

	private final String descrizione;

	Categoria(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}
}
