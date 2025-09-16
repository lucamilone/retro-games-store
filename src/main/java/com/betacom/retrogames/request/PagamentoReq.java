package com.betacom.retrogames.request;

import java.math.BigDecimal;

import com.betacom.retrogames.model.enums.StatoPagamento;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PagamentoReq {

	@NotNull(groups = { OnUpdate.class, OnDelete.class }, message = "Id obbligatorio per l'aggiornamento")
	@Positive(groups = { OnUpdate.class, OnDelete.class }, message = "Id deve essere positivo")
	private Integer id;

	@NotNull(groups = OnCreate.class, message = "OrdineId obbligatorio")
	@Positive(message = "OrdineId deve essere positivo")
	private Integer ordineId;

	@NotNull(groups = OnCreate.class, message = "Totale obbligatorio")
	@DecimalMin(groups = { OnCreate.class,
			OnUpdate.class }, value = "0.01", inclusive = true, message = "Totale deve essere maggiore di 0")
	@Digits(groups = { OnCreate.class,
			OnUpdate.class }, integer = 10, fraction = 2, message = "Totale non valido (max 10 cifre intere e 2 decimali)")
	private BigDecimal totale;

	@NotNull(groups = OnCreate.class, message = "MetodoPagamentoId obbligatorio")
	@Positive(message = "MetodoPagamentoId deve essere positivo")
	private Integer metodoPagamentoId;

	@Null(groups = OnCreate.class, message = "Stato pagamento viene assegnato automaticamente alla creazione")
	@NotNull(groups = OnUpdate.class, message = "Stato pagamento obbligatorio")
	private StatoPagamento statoPagamento;
}