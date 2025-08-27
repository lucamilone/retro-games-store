package com.betacom.retrogames.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CarrelloRigaDTO {

	private Integer id;
	private String prodotto;
	private Integer quantita;
}