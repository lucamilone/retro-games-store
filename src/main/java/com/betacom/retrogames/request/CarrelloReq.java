package com.betacom.retrogames.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CarrelloReq {

	private Integer id;
	private Integer accountId;
	private List<CarrelloRigaReq> righe;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}