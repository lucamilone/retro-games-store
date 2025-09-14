package com.betacom.retrogames.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Ordine;

@Repository
public interface OrdineRepository extends JpaRepository<Ordine, Integer> {

	Optional<Ordine> findByAccountId(Integer accountId);

	List<Ordine> findAllByAccountId(Integer accountId);
}