package com.betacom.retrogames.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.betacom.retrogames.model.CarrelloRiga;

@Repository
public interface CarrelloRigaRepository extends JpaRepository<CarrelloRiga, Integer> {
	List<CarrelloRiga> findAllByCarrelloId(Integer carrelloId);
}