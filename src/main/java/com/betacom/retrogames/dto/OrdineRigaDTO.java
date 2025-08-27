package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class OrdineRigaDTO 
{
    private Integer id;
    private String prodotto;
    private Integer quantita;
    private BigDecimal prezzoUnitario;
    private BigDecimal totale;
}