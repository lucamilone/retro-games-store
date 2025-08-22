package com.betacom.retrogames.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@Embeddable
public class Indirizzo {

	@NotBlank(message = "La via è obbligatoria")
	@Column(length = 100)
	private String via;

	@NotBlank(message = "La città è obbligatoria")
	@Column(length = 50)
	private String citta;

	@NotBlank(message = "Il CAP è obbligatorio")
	@Pattern(regexp = "\\d{5}", message = "Il CAP deve avere 5 cifre")
	@Column(length = 5)
	private String cap;

	@NotBlank(message = "Il paese è obbligatorio")
	@Column(length = 50)
	private String paese;
}
