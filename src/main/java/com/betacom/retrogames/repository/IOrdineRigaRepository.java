package com.betacom.retrogames.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.betacom.retrogames.model.OrdineRiga;

@Repository
public interface IOrdineRigaRepository extends JpaRepository<OrdineRiga, Integer> 
{
	Optional<OrdineRiga> findByOrdineId(Integer ordineId);
}