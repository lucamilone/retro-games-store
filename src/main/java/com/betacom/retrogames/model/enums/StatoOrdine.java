package com.betacom.retrogames.model.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

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

	@JsonCreator
	public static StatoOrdine fromString(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String normalized = value.trim();
		return Arrays.stream(values()).filter(s -> s.name().equalsIgnoreCase(normalized)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Stato ordine non valido: " + value));
	}

	/**
	 * Controlla se la transizione da questo stato a nuovoStato è valida.
	 */
	public boolean isTransizioneValidaVerso(StatoOrdine nuovoStato) {
		if (this == nuovoStato) {
			return true; // Permette di "riconfermare" lo stesso stato
		}

		switch (this) {
		case IN_ATTESA:
			return nuovoStato == PAGATO || nuovoStato == ANNULLATO;
		case PAGATO:
			return nuovoStato == SPEDITO || nuovoStato == ANNULLATO;
		case SPEDITO:
			return nuovoStato == CONSEGNATO;
		case CONSEGNATO:
		case ANNULLATO:
			return false; // Stati finali non possono cambiare
		default:
			return false;
		}
	}
}