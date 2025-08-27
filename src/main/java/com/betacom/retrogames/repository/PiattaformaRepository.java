package com.betacom.retrogames.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Piattaforma;

@Repository
public interface PiattaformaRepository extends JpaRepository<Piattaforma, Integer> {

}
