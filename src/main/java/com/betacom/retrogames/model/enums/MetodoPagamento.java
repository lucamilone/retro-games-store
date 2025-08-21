package com.betacom.retrogames.model.enums;

public enum MetodoPagamento {
	CARTA_CREDITO("Carta Di Credito"), PAYPAL("PayPal"), BONIFICO("Bonifico Bancario");

	private final String descrizione;

	MetodoPagamento(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}
}
