package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrdineDTO {

	private Integer id;
	private String statoOrdine;
	private Integer accountId;
	private List<OrdineRigaDTO> righe;
	private IndirizzoDTO indirizzoSpedizione;
	private PagamentoDTO pagamento;
	private BigDecimal totale; // Somma del subTotale di tutte le righe

	// Campi aggiuntivi utili al frontend
	private Integer totaleQuantita; // Somma delle quantità di tutte le righe
}