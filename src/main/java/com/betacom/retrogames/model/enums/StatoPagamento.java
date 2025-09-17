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

	/**
	 * Controlla se la transizione da questo stato a nuovoStato è valida.
	 */
	public boolean isTransizioneValidaVerso(StatoPagamento nuovoStato) {
		if (this == nuovoStato) {
			return true; // Permette di "riconfermare" lo stesso stato
		}

		switch (this) {
		case IN_ATTESA:
			return nuovoStato == SUCCESSO || nuovoStato == FALLITO;
		case SUCCESSO:
		case FALLITO:
			return false; // Stati finali non possono cambiare
		default:
			return false;
		}
	}
}