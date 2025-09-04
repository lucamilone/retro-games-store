package com.betacom.retrogames.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categoria")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false, unique = true)
	private String nome;

	@OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
	private Set<Prodotto> prodotti = new HashSet<>();

	@Column(nullable = false)
	private boolean attivo;

	public void addProdotto(Prodotto prodotto) {
		prodotti.add(prodotto);
		prodotto.setCategoria(this);
	}

	public void removeProdotto(Prodotto prodotto) {
		prodotti.remove(prodotto);
		prodotto.setCategoria(null);
	}
}
