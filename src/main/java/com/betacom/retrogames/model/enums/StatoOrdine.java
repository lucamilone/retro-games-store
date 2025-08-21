package com.betacom.retrogames.model.enums;

public enum StatoOrdine {
	IN_ATTESA("In attesa", false, false), PAGATO("Pagato", false, false), SPEDITO("Spedito", false, true),
	CONSEGNATO("Consegnato", true, true), ANNULLATO("Annullato", true, false);

	private final String descrizione;
	private final boolean finale;
	private final boolean spedito;

	StatoOrdine(String descrizione, boolean finale, boolean spedito) {
		this.descrizione = descrizione;
		this.finale = finale;
		this.spedito = spedito;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public boolean isFinale() {
		return finale;
	}

	public boolean isSpedito() {
		return spedito;
	}
}