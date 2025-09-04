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
public class OrdineDTO {

	private Integer id;
	private String statoOrdine;
	private Integer accountId;
	private List<OrdineRigaDTO> righe;
	private IndirizzoDTO indirizzoSpedizione;
	private PagamentoDTO pagamento;
	private BigDecimal totale;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}