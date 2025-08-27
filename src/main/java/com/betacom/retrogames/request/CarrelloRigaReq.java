package com.betacom.retrogames.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CarrelloRigaReq 
{
    private Integer id;
    private Integer carrelloId;
    private Integer prodottoId;
    private Integer quantita;
}
