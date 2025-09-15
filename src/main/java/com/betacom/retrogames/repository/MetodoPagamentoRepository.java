package com.betacom.retrogames.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.MetodoPagamento;

@Repository
public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamento, Integer> {

	Optional<MetodoPagamento> findByToken(String token);

	List<MetodoPagamento> findByAccountIdAndAttivoTrue(Integer accountId);
}