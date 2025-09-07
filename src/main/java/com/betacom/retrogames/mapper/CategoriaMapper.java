package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.model.Categoria;

@Mapper(componentModel = "spring", uses = { ProdottoMapper.class })
public interface CategoriaMapper {

	CategoriaDTO toDto(Categoria categoria);

	List<CategoriaDTO> toDtoList(List<Categoria> categorie);
}
