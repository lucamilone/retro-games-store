package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.model.Prodotto;

@Mapper(componentModel = "spring", uses = { CategoriaMapper.class, PiattaformaMapper.class })
public interface ProdottoMapper {

	@Mapping(target = "categoria", source = "categoria.nome")
	ProdottoDTO toDto(Prodotto prodotto);

	List<ProdottoDTO> toDtos(List<ProdottoDTO> prodotti);
}
