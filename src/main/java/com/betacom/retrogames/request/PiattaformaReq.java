package com.betacom.retrogames.request;

import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PiattaformaReq {

    @NotNull(groups = OnUpdate.class, message = "Id obbligatorio")
    @Positive(groups = OnUpdate.class, message = "Id deve essere positivo")
    private Integer id;

    @NotBlank(groups = OnCreate.class, message = "Codice obbligatorio")
    @Size(max = 20, message = "Codice non può superare 20 caratteri")
    private String codice;

    @NotBlank(groups = OnCreate.class, message = "Nome obbligatorio")
    @Size(max = 150, message = "Nome non può superare 150 caratteri")
    private String nome;

    @NotNull(groups = OnCreate.class, message = "Anno di uscita obbligatorio")
    @Min(value = 1970, message = "Anno di uscita non può essere inferiore al 1970")
    @Max(value = 2006, message = "Anno di uscita non può essere superiore al 2006")
    private Integer annoUscitaPiattaforma;
}