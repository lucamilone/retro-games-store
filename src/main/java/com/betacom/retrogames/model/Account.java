package com.betacom.retrogames.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.betacom.retrogames.model.enums.Ruolo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false)
	private String nome;

	@Column(length = 100, nullable = false)
	private String cognome;

	@Column(nullable = false)
	private String indirizzo;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private Ruolo ruolo;

	@OneToOne(mappedBy = "account", cascade = CascadeType.REMOVE)
	private Credenziale credenziale;

	@OneToOne(mappedBy = "account", cascade = CascadeType.REMOVE)
	private Carrello carrello;

	@CreationTimestamp
	@Column(name = "data_ora_creazione", nullable = false, updatable = false)
	private LocalDateTime dataOraCreazione;

	@UpdateTimestamp
	@Column(name = "data_ora_aggiornamento")
	private LocalDateTime dataOraAggiornamento;
}
