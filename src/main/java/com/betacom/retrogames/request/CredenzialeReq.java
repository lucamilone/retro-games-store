package com.betacom.retrogames.request;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.constraints.Email;
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
public class CredenzialeReq {

	@NotNull(groups = OnUpdate.class, message = "Id obbligatorio")
	@Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
	private Integer id;

	@NotBlank(groups = OnCreate.class, message = "Email obbligatoria")
	@Email(message = "Formato email non valido")
	@Size(max = 255, message = "Email non può superare 255 caratteri")
	private String email;

	@NotBlank(groups = OnCreate.class, message = "Password obbligatoria")
	@Size(min = 8, max = 255, message = "La password deve avere almeno 8 caratteri e massimo 255")
	private String password;
}