package com.betacom.retrogames.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountDTO {

	private Integer id;
	private String nome;
	private String cognome;
	private IndirizzoDTO indirizzo;
	private Set<MetodoPagamentoDTO> metodiPagamento;
	private String ruolo;
	private CredenzialeDTO credenziale;
	private CarrelloDTO carrello;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}