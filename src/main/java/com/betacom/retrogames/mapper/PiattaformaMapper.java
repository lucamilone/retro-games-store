package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.model.Piattaforma;

@Mapper(componentModel = "spring")
public interface PiattaformaMapper {

	PiattaformaDTO toDto(Piattaforma piattaforma);

	PiattaformaDTO toDtoFromCached(CachedPiattaforma cached);

	List<PiattaformaDTO> toDtoList(List<Piattaforma> piattaforme);
}
