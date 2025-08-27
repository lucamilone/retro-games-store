package com.betacom.retrogames.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountReq {

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