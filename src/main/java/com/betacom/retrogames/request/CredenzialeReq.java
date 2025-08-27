package com.betacom.retrogames.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CredenzialeReq {

	private Integer id;
	private String accountId;
	private String email;
	private String passwordHash;
	private LocalDateTime ultimoLogin;
}