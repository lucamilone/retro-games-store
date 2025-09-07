package com.betacom.retrogames.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CredenzialeDTO {

	private Integer id;
	private Integer accountId;
	private String email;
	private LocalDateTime ultimoLogin;
}