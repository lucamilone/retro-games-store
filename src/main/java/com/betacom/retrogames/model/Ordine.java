package com.betacom.retrogames.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.betacom.retrogames.model.enums.StatoOrdine;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrdineRiga> righe = new ArrayList<>();

	@Embedded
	@Column(name = "indirizzo_spedizione", nullable = false)
	private Indirizzo indirizzoSpedizione;

	@OneToOne(mappedBy = "ordine", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Pagamento pagamento;

	@CreationTimestamp
	@Column(name = "creato_il", nullable = false, updatable = false)
	private LocalDateTime creatoIl;

	@UpdateTimestamp
	@Column(name = "aggiornato_il")
	private LocalDateTime aggiornatoIl;

	public BigDecimal getTotale() {
		return righe == null ? BigDecimal.ZERO
				: righe.stream().map(OrdineRiga::getTotale).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public void addRiga(OrdineRiga riga) {
		righe.add(riga);
		riga.setOrdine(this);
	}

	public void removeRiga(OrdineRiga riga) {
		righe.remove(riga);
		riga.setOrdine(null);
	}
}
