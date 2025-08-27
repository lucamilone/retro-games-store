package com.betacom.retrogames.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class PiattaformaDTO 
{
    private Integer id;
    private String codice;
    private String nome;
    private Integer annoUscitaPiattaforma;
}