package com.betacom.retrogames.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class MetodoPagamentoDTO 
{
    private Integer id;
    private String account;
    private String tipoMetodoPagamento;
    private String token;
    private Boolean metodoDefault;
    private LocalDateTime creatoIl;
    private LocalDateTime aggiornatoIl;
}