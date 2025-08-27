package com.betacom.retrogames.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Credenziale;

@Repository
public interface CredenzialeRepository extends JpaRepository<Credenziale, Integer> {

	Optional<Credenziale> findByEmail(String email);
}