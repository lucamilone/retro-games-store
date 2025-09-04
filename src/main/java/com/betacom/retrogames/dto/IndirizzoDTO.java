package com.betacom.retrogames.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class IndirizzoDTO {

	private String via;
	private String citta;
	private String cap;
	private String paese;
}