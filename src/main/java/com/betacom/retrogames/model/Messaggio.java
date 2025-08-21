package com.betacom.retrogames.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "messaggi_sistema")
public class Messaggio {

	@EmbeddedId
	private MessaggioId msgId;

	private String messaggio;
}
