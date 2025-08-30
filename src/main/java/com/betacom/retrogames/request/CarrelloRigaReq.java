package com.betacom.retrogames.request;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CarrelloRigaReq {

	@NotNull(groups = OnUpdate.class, message = "Id obbligatorio")
	@Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
	private Integer id;

	@NotNull(groups = OnCreate.class, message = "CarrelloId obbligatorio")
	@Positive(groups = OnCreate.class, message = "CarrelloId deve essere positivo")
	private Integer carrelloId;

	@NotNull(groups = OnCreate.class, message = "ProdottoId obbligatorio")
	@Positive(groups = OnCreate.class, message = "ProdottoId deve essere positivo")
	private Integer prodottoId;

	@NotNull(groups = OnCreate.class, message = "Quantità obbligatoria")
	@Positive(message = "Quantità deve essere maggiore di zero")
	private Integer quantita;
}
