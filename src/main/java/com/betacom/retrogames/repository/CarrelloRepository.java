package com.betacom.retrogames.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Carrello;

@Repository
public interface CarrelloRepository extends JpaRepository<Carrello, Integer> {

	Optional<Carrello> findByAccountId(Integer accountId);
}