package com.betacom.retrogames.request;

import com.betacom.retrogames.model.enums.StatoPagamento;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class PagamentoReq {

    @NotNull(groups = OnUpdate.class, message = "Id obbligatorio per l'aggiornamento")
    @Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
    private Integer id;

    @NotNull(groups = OnCreate.class, message = "OrdineId obbligatorio")
    @Positive(message = "OrdineId deve essere positivo")
    private Integer ordineId;

    @NotNull(groups = OnCreate.class, message = "Totale obbligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "Totale deve essere maggiore di 0")
    @Digits(integer = 10, fraction = 2, message = "Totale non valido (max 10 cifre intere e 2 decimali)")
    private BigDecimal totale;

    @NotNull(groups = OnCreate.class, message = "MetodoPagamentoId obbligatorio")
    @Positive(message = "MetodoPagamentoId pagamento deve essere positivo")
    private Integer metodoPagamentoId;

    @NotNull(groups = OnCreate.class, message = "Stato pagamento obbligatorio")
    private StatoPagamento statoPagamento;
}