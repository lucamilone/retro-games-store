package com.betacom.retrogames.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@Embedded
	private Indirizzo indirizzo;

	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<MetodoPagamento> metodiPagamento = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ruolo_id", nullable = false)
	private Ruolo ruolo;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Credenziale credenziale;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Carrello carrello;

	@CreationTimestamp
	@Column(name = "creato_il", nullable = false, updatable = false)
	private LocalDateTime creatoIl;

	@UpdateTimestamp
	@Column(name = "aggiornato_il")
	private LocalDateTime aggiornatoIl;

	public void addMetodoPagamento(MetodoPagamento mp) {
		metodiPagamento.add(mp);
		mp.setAccount(this);
	}

	public void removeMetodoPagamento(MetodoPagamento mp) {
		metodiPagamento.remove(mp);
		mp.setAccount(null);
	}
}
