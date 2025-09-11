package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.model.Categoria;
import com.betacom.retrogames.model.Prodotto;

@Mapper(componentModel = "spring", uses = { PiattaformaMapper.class })
public interface ProdottoMapper {

	ProdottoDTO toDto(Prodotto prodotto);

	List<ProdottoDTO> toDtoList(List<Prodotto> prodotti);

	default String map(Categoria categoria) {
		return categoria != null ? categoria.getNome() : null;
	}
}
