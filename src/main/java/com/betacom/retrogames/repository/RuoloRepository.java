package com.betacom.retrogames.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Ruolo;

@Repository
public interface RuoloRepository extends JpaRepository<Ruolo, Integer> {

	Optional<Ruolo> findByNome(String nome);
}
