package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.model.Categoria;

@Mapper(componentModel = "spring", uses = { ProdottoMapper.class })
public interface CategoriaMapper {

	CategoriaDTO toDto(Categoria categoria);

	@Mapping(target = "prodotti", ignore = true)
	CategoriaDTO toDtoFromCached(CachedCategoria cached);

	List<CategoriaDTO> toDtoList(List<Categoria> categorie);

	@Named("toNome")
	default String map(Categoria categoria) {
		return categoria != null ? categoria.getNome() : null;
	}
}
