package com.betacom.retrogames.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IndirizzoDTO {

	private String via;
	private String citta;
	private String cap;
	private String paese;
}