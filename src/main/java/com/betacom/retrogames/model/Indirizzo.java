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

	@NotBlank(message = "Via obbligatoria")
	@Column(length = 100, nullable = false)
	private String via;

	@NotBlank(message = "Città obbligatoria")
	@Column(length = 50, nullable = false)
	private String citta;

	@NotBlank(message = "CAP obbligatorio")
	@Pattern(regexp = "\\d{5}", message = "CAP deve essere un numero di 5 cifre")
	@Column(length = 5, nullable = false)
	private String cap;

	@NotBlank(message = "Paese obbligatorio")
	@Column(length = 50, nullable = false)
	private String paese;
}
