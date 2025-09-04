package com.betacom.retrogames.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class AccountDTO {

	private Integer id;
	private String nome;
	private String cognome;
	private IndirizzoDTO indirizzo;
	private String ruolo;
	private CredenzialeDTO credenziale;
	private CarrelloDTO carrello;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}