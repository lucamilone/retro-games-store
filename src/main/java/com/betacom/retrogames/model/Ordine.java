package com.betacom.retrogames.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.betacom.retrogames.model.enums.StatoOrdine;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "ordine")
public class Ordine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private StatoOrdine statoOrdine = StatoOrdine.IN_ATTESA;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	private Account account;

	@OneToMany(mappedBy = "ordine", cascade = CascadeType.REMOVE)
	private List<OrdineRiga> ordineRiga;

	@OneToOne(mappedBy = "ordine", cascade = CascadeType.REMOVE)
	private Pagamento pagamento;

	@CreationTimestamp
	@Column(name = "data_ora_creazione", nullable = false, updatable = false)
	private LocalDateTime dataOraCreazione;

	@UpdateTimestamp
	@Column(name = "data_ora_aggiornamento")
	private LocalDateTime dataOraAggiornamento;

	public BigDecimal getTotale() {
		return ordineRiga.stream().map(OrdineRiga::getTotale).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
