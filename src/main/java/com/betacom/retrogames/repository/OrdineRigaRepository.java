package com.betacom.retrogames.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.OrdineRiga;

@Repository
public interface OrdineRigaRepository extends JpaRepository<OrdineRiga, Integer> {

	List<OrdineRiga> findAllByOrdineId(Integer ordineId);
}