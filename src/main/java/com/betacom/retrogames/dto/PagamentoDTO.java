package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class PagamentoDTO 
{
    private Integer id;
    private String ordine;
    private BigDecimal totale;
    private String metodoPagamento;
    private String statoPagamento;
    private LocalDateTime creatoIl;
    private LocalDateTime aggiornatoIl;
}