package com.betacom.retrogames.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IndirizzoReq {

	private String via;
	private String citta;
	private String cap;
	private String paese;
}