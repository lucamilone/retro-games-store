package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.model.Piattaforma;

@Mapper(componentModel = "spring", uses = { ProdottoMapper.class })
public interface PiattaformaMapper {

	@Mapping(target = "prodotti", ignore = true)
	PiattaformaDTO toDto(Piattaforma piattaforma);

	@Mapping(target = "prodotti", ignore = true)
	PiattaformaDTO toDtoFromCached(CachedPiattaforma cached);

	List<PiattaformaDTO> toDtoList(List<Piattaforma> piattaforme);
}
