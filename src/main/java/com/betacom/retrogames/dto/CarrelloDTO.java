package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CarrelloDTO {

	private Integer id;
	private Integer accountId;
	private List<CarrelloRigaDTO> righe;

	// Campi aggiuntivi utili al frontend
	private Integer totaleQuantita; // Somma delle quantità di tutte le righe
	private BigDecimal totale; // Somma del subTotale di tutte le righe
}