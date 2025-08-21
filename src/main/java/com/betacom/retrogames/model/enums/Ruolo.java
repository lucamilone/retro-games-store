package com.betacom.retrogames.model.enums;

public enum Ruolo {
	ADMIN("Amministratore"), CLIENTE("Cliente");

	private final String descrizione;

	Ruolo(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public boolean isAdmin() {
		return this == ADMIN;
	}
}
