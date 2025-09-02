package com.betacom.retrogames.request;

import java.math.BigDecimal;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrdineRigaReq {

	@NotNull(groups = OnUpdate.class, message = "Id obbligatorio per l'aggiornamento")
	@Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
	private Integer id;

	@NotNull(groups = OnCreate.class, message = "OrdineId obbligatorio")
	@Positive(message = "OrdineId deve essere positivo")
	private Integer ordineId;

	@NotNull(groups = OnCreate.class, message = "ProdottoId obbligatorio")
	@Positive(message = "ProdottoId deve essere positivo")
	private Integer prodottoId;

	@NotNull(groups = OnCreate.class, message = "Quantità obbligatoria")
	@Positive(message = "Quantità deve essere maggiore di zero")
	private Integer quantita;

	@NotNull(groups = OnCreate.class, message = "Prezzo unitario obbligatorio")
	@DecimalMin(value = "0.01", inclusive = true, message = "Prezzo unitario deve essere maggiore di zero")
	@Digits(integer = 10, fraction = 2, message = "Prezzo unitario non valido (max 10 cifre intere e 2 decimali)")
	private BigDecimal prezzoUnitario;
}