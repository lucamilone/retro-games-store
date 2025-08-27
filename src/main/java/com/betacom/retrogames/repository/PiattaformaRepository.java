package com.betacom.retrogames.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Piattaforma;

@Repository
public interface PiattaformaRepository extends JpaRepository<Piattaforma, Integer> {

	Optional<Piattaforma> findByCodice(String codice);
}
