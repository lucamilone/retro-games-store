package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CarrelloDTO {

	private Integer id;
	private Integer accountId;
	private List<CarrelloRigaDTO> righe;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;

	// Campi aggiuntivi utili al frontend
	private Integer totaleQuantita; // Somma delle quantità di tutte le righe
	private BigDecimal totaleCarrello; // Somma di tutte le righe
}