package com.betacom.retrogames.request;

import java.math.BigDecimal;
import java.util.Set;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProdottoReq {

	@NotNull(groups = OnUpdate.class, message = "Id obbligatorio")
	@Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
	private Integer id;

	@NotBlank(groups = OnCreate.class, message = "SKU obbligatorio")
	@Size(max = 50, message = "SKU non può superare 50 caratteri")
	private String sku;

	@NotBlank(groups = OnCreate.class, message = "Nome obbligatorio")
	@Size(max = 200, message = "Nome non può superare 200 caratteri")
	private String nome;

	@NotNull(groups = OnCreate.class, message = "Categoria obbligatoria")
	@Positive(message = "CategoriaId deve essere positivo")
	private Integer categoriaId;

	@NotEmpty(groups = OnCreate.class, message = "Almeno una piattaforma è richiesta")
	@Valid
	private Set<Integer> piattaformaId;

	@NotBlank(groups = OnCreate.class, message = "Descrizione obbligatoria")
	private String descrizione;

	@NotNull(groups = OnCreate.class, message = "Anno di uscita obbligatorio")
	@Min(value = 1970, message = "Anno di uscita non può essere inferiore al 1970")
	@Max(value = 2006, message = "Anno di uscita non può essere superiore all'anno 2006")
	private Integer annoUscita;

	@NotNull(groups = OnCreate.class, message = "Prezzo obbligatorio")
	@DecimalMin(value = "0.01", inclusive = true, message = "Prezzo deve essere maggiore di 0")
	@Digits(integer = 10, fraction = 2, message = "Prezzo non valido (max 10 cifre intere e 2 decimali)")
	private BigDecimal prezzo;
}