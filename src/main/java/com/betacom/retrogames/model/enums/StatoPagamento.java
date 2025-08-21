package com.betacom.retrogames.model.enums;

public enum StatoPagamento {
	IN_ATTESA("In attesa", false), SUCCESSO("Successo", true), FALLITO("Fallito", true);

	private final String descrizione;
	private final boolean finale;

	StatoPagamento(String descrizione, boolean finale) {
		this.descrizione = descrizione;
		this.finale = finale;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public boolean isFinale() {
		return finale;
	}
}