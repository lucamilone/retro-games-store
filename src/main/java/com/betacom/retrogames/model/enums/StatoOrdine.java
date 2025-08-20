package com.betacom.retrogames.model.enums;

public enum StatoOrdine {
	IN_ATTESA("In attesa"), PAGATO("Pagato"), SPEDITO("Spedito"), CONSEGNATO("Consegnato"), ANNULLATO("Annullato");

	private final String descrizione;

	StatoOrdine(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}
}