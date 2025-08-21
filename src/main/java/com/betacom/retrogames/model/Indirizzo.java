package com.betacom.retrogames.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Embeddable
public class Indirizzo {

	@NotBlank(message = "La via è obbligatoria")
	private String via;

	@NotBlank(message = "La città è obbligatoria")
	private String citta;

	@NotBlank(message = "Il CAP è obbligatorio")
	@Pattern(regexp = "\\d{5}", message = "Il CAP deve avere 5 cifre")
	private String cap;

	@NotBlank(message = "Il paese è obbligatorio")
	private String paese;
}
