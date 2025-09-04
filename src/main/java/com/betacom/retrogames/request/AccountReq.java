package com.betacom.retrogames.request;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountReq {

	@NotNull(groups = OnUpdate.class, message = "Id obbligatorio")
	@Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
	private Integer id;

	@NotBlank(groups = OnCreate.class, message = "Nome obbligatorio")
	@Size(max = 100, message = "Nome non può superare 100 caratteri")
	private String nome;

	@NotBlank(groups = OnCreate.class, message = "Cognome obbligatorio")
	@Size(max = 100, message = "Cognome non può superare 100 caratteri")
	private String cognome;

	@NotNull(groups = OnCreate.class, message = "Indirizzo obbligatorio")
	@Valid
	private IndirizzoReq indirizzo;

	@NotNull(groups = OnCreate.class, message = "Ruolo obbligatorio")
	@Positive(message = "RuoloId deve essere positivo")
	private Integer ruoloId;
}