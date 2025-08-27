package com.betacom.retrogames.request;

import java.math.BigDecimal;
import java.util.Set;
import com.betacom.retrogames.model.Piattaforma;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProdottoReq 
{
    private Integer id;
    private String sku;
    private String nome;
    private Integer categoriaId;
    private Set<Piattaforma> piattaformaIds;
    private String descrizione;
    private Integer annoUscita;
    private BigDecimal prezzo;
}