package com.betacom.retrogames.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountReq 
{
    private Integer id;
    private String nome;
    private String cognome;
    private IndirizzoReq indirizzo;
    private Integer ruoloId;
    private CredenzialeReq credenziale;
    private CarrelloReq carrello;
    private LocalDateTime creatoIl;
    private LocalDateTime aggiornatoIl;
}