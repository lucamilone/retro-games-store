package com.betacom.retrogames.request;

import com.betacom.retrogames.model.enums.StatoOrdine;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrdineReq {

	@NotNull(groups = { OnUpdate.class, OnDelete.class }, message = "Id obbligatorio per l'aggiornamento")
	@Positive(groups = { OnUpdate.class, OnDelete.class }, message = "Id deve essere positivo")
	private Integer id;

	@Null(groups = OnCreate.class, message = "Stato ordine viene assegnato automaticamente alla creazione")
	@NotNull(groups = OnUpdate.class, message = "Stato ordine obbligatorio")
	private StatoOrdine statoOrdine;

	@NotNull(groups = OnCreate.class, message = "AccountId obbligatorio")
	@Positive(message = "AccountId deve essere positivo")
	private Integer accountId;

	@Valid
	private IndirizzoReq indirizzoSpedizione;

	@Valid
	private PagamentoReq pagamento;
}