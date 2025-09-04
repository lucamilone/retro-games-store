package com.betacom.retrogames.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class CredenzialeDTO {

	private Integer id;
	private Integer accountId;
	private String email;
	private LocalDateTime ultimoLogin;
}