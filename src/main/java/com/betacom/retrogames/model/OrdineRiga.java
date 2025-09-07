package com.betacom.retrogames.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ordine_riga")
@Check(constraints = "quantita > 0")
public class OrdineRiga {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ordine_id", nullable = false)
	private Ordine ordine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prodotto_id", nullable = false)
	private Prodotto prodotto;

	@Column(nullable = false)
	@Check(constraints = "quantita > 0")
	private Integer quantita;

	@Column(name = "prezzo_unitario", precision = 10, scale = 2, nullable = false)
	private BigDecimal prezzoUnitario;

	@CreationTimestamp
	@Column(name = "creato_il", nullable = false, updatable = false)
	private LocalDateTime creatoIl;

	@UpdateTimestamp
	@Column(name = "aggiornato_il")
	private LocalDateTime aggiornatoIl;

	public BigDecimal getSubTotale() {
		return prezzoUnitario == null ? BigDecimal.ZERO : prezzoUnitario.multiply(BigDecimal.valueOf(quantita));
	}
}
