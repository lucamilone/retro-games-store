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
public class MetodoPagamentoReq {

	@NotNull(groups = { OnUpdate.class, OnDelete.class }, message = "Id obbligatorio")
	@Positive(groups = { OnUpdate.class, OnDelete.class }, message = "Id deve essere positivo")
	private Integer id;

	@NotNull(groups = OnCreate.class, message = "AccountId obbligatorio")
	@Positive(message = "AccountId deve essere positivo")
	private Integer accountId;

	@NotNull(groups = OnCreate.class, message = "Tipo metodo pagamento obbligatorio")
	@Positive(message = "TipoMetodoPagamentoId deve essere positivo")
	private Integer tipoMetodoPagamentoId;

	@NotBlank(groups = OnCreate.class, message = "Token obbligatorio")
	@Size(max = 255, message = "Token non può superare 255 caratteri")
	private String token;

	private Boolean metodoDefault;

	private Boolean attivo;
}