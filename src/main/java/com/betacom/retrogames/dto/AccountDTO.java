package com.betacom.retrogames.dto;

import java.util.List;

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
	private List<MetodoPagamentoDTO> metodiPagamento;
	private String ruolo;
	private CredenzialeDTO credenziale;
	private CarrelloDTO carrello;
	private Boolean attivo;
}