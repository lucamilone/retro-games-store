package com.betacom.retrogames.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.betacom.retrogames.model.Messaggio;
import com.betacom.retrogames.model.MessaggioId;

public interface IMessaggioRepository extends JpaRepository<Messaggio, MessaggioId> {

}
