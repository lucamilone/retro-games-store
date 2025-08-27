package com.betacom.retrogames.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.TipoMetodoPagamento;

@Repository
public interface TipoMetodoPagamentoRepository extends JpaRepository<TipoMetodoPagamento, Integer> {

}
