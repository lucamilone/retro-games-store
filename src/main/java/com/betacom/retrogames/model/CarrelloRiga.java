package com.betacom.retrogames.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carrello_riga", uniqueConstraints = @UniqueConstraint(columnNames = { "carrello_id", "prodotto_id" }))
@Check(constraints = "quantita > 0")
public class CarrelloRiga {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "carrello_id", nullable = false)
	private Carrello carrello;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prodotto_id", nullable = false)
	private Prodotto prodotto;

	@Column(nullable = false)
	@Check(constraints = "quantita > 0")
	private Integer quantita;

	@CreationTimestamp
	@Column(name = "creato_il", nullable = false, updatable = false)
	private LocalDateTime creatoIl;
}
