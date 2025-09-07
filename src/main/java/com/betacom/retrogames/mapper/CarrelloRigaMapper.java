package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.CarrelloRigaDTO;
import com.betacom.retrogames.model.CarrelloRiga;

@Mapper(componentModel = "spring", uses = { ProdottoMapper.class })
public interface CarrelloRigaMapper {

	@Mapping(target = "carrelloId", source = "carrello.id")
	@Mapping(target = "subTotale", ignore = true)
	CarrelloRigaDTO toDto(CarrelloRiga riga);

	List<CarrelloRigaDTO> toDtoList(List<CarrelloRiga> righe);
}
