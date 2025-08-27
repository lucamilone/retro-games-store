package com.betacom.retrogames.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrdineRigaReq 
{
    private Integer id;
    private Integer ordineId;
    private Integer prodottoId;
    private Integer quantita;
    private BigDecimal prezzoUnitario;
}