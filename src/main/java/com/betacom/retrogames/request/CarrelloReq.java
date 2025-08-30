package com.betacom.retrogames.request;

import java.util.List;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CarrelloReq {

	@NotNull(groups = OnUpdate.class, message = "Id obbligatorio")
	@Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
	private Integer id;

	@NotNull(groups = OnCreate.class, message = "AccountId obbligatorio")
	@Positive(groups = OnCreate.class, message = "AccountId deve essere positivo")
	private Integer accountId;

	@Valid
	private List<CarrelloRigaReq> righe;
}