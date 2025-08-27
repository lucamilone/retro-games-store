package com.betacom.retrogames.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class CarrelloRigaDTO 
{
    private Integer id;
    private String prodotto;
    private Integer quantita;
}