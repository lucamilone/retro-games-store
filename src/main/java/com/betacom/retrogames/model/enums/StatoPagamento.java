package com.betacom.retrogames.model.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

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

	@JsonCreator
	public static StatoPagamento fromString(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String normalized = value.trim();
		return Arrays.stream(values()).filter(s -> s.name().equalsIgnoreCase(normalized)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Stato pagamento non valido: " + value));
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