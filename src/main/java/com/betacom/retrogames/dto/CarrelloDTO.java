package com.betacom.retrogames.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CarrelloDTO {

	private Integer id;
	private String account;
	private List<CarrelloRigaDTO> righe;
	private LocalDateTime creatoIl;
	private LocalDateTime aggiornatoIl;
}