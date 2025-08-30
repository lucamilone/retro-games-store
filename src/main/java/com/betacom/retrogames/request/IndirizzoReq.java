package com.betacom.retrogames.request;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IndirizzoReq {

	@NotBlank(groups = OnCreate.class, message = "Via obbligatoria")
	@Size(max = 100, message = "Via non può superare 100 caratteri")
	private String via;

	@NotBlank(groups = OnCreate.class, message = "Città obbligatoria")
	@Size(max = 50, message = "Città non può superare 50 caratteri")
	private String citta;

	@NotBlank(groups = OnCreate.class, message = "CAP obbligatorio")
	@Pattern(regexp = "^[0-9]{5}$", message = "CAP deve essere un numero di 5 cifre")
	private String cap;

	@NotBlank(groups = OnCreate.class, message = "Paese obbligatorio")
	@Size(max = 50, message = "Paese non può superare 50 caratteri")
	private String paese;
}