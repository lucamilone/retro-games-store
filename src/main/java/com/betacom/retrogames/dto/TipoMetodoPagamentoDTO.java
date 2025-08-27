package com.betacom.retrogames.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class TipoMetodoPagamentoDTO 
{
    private Integer id;
    private String nome;
}