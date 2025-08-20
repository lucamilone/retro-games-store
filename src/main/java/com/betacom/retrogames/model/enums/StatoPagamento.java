package com.betacom.retrogames.model.enums;

public enum StatoPagamento {
	IN_ATTESA("In attesa"), SUCCESSO("Successo"), FALLITO("Fallito");

	private final String descrizione;

	StatoPagamento(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}
}