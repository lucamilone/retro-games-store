package com.betacom.retrogames.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Prodotto;

@Repository
public interface ProdottoRepository extends JpaRepository<Prodotto, Integer> {

	Optional<Prodotto> findBySku(String sku);
	
	List<Prodotto> findByAttivoTrue();
}