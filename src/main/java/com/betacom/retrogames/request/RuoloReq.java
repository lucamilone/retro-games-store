package com.betacom.retrogames.request;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

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
public class RuoloReq {

	@NotNull(groups = { OnUpdate.class, OnDelete.class }, message = "Id obbligatorio")
	@Positive(groups = { OnUpdate.class, OnDelete.class }, message = "Id deve essere positivo")
	private Integer id;

	@NotBlank(groups = { OnCreate.class, OnUpdate.class }, message = "Nome obbligatorio")
	@Size(max = 50, message = "Nome non può superare 50 caratteri")
	private String nome;

	private Boolean attivo;
}