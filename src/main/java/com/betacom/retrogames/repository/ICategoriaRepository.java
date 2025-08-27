package com.betacom.retrogames.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.betacom.retrogames.model.Categoria;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> 
{
    Optional<Categoria> findByNome(String nome);
}