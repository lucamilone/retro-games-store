package com.betacom.retrogames.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PiattaformaReq {

	private Integer id;
	private String codice;
	private String nome;
	private Integer annoUscitaPiattaforma;
}