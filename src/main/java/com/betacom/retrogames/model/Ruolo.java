package com.betacom.retrogames.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ruolo")
public class Ruolo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 50, nullable = false, unique = true)
	private String nome;

	@Column(nullable = false)
	private boolean attivo;

	@CreationTimestamp
	@Column(name = "creato_il", nullable = false, updatable = false)
	private LocalDateTime creatoIl;

	@UpdateTimestamp
	@Column(name = "aggiornato_il")
	private LocalDateTime aggiornatoIl;
}
