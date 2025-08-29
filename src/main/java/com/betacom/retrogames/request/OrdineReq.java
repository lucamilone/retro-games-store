package com.betacom.retrogames.request;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.model.enums.StatoOrdine;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class OrdineReq {

    @NotNull(groups = OnUpdate.class, message = "Id obbligatorio per l'aggiornamento")
    @Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
    private Integer id;

    @NotNull(groups = OnCreate.class, message = "Stato ordine obbligatorio")
    private StatoOrdine statoOrdine;

    @NotNull(groups = OnCreate.class, message = "AccountId obbligatorio")
    @Positive(message = "AccountId deve essere positivo")
    private Integer accountId;

    @NotEmpty(groups = OnCreate.class, message = "Almeno una riga è richiesta")
    private List<OrdineRigaReq> righe;

    @NotNull(groups = OnCreate.class, message = "Indirizzo di spedizione obbligatorio")
    @Valid
    private IndirizzoReq indirizzoSpedizione;

    @NotNull(groups = OnCreate.class, message = "Pagamento obbligatorio")
    @Valid
    private PagamentoReq pagamento;
}