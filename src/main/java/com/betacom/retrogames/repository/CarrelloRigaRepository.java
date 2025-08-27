package com.betacom.retrogames.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.CarrelloRiga;

@Repository
public interface CarrelloRigaRepository extends JpaRepository<CarrelloRiga, Integer> {

	Optional<CarrelloRiga> findByCarrelloId(Integer carrelloId);
}