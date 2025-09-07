package com.betacom.retrogames.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.CarrelloDTO;
import com.betacom.retrogames.model.Carrello;

@Mapper(componentModel = "spring", uses = { CarrelloRigaMapper.class })
public interface CarrelloMapper {

	@Mapping(target = "accountId", source = "account.id")
	@Mapping(target = "totaleQuantita", ignore = true)
	@Mapping(target = "totale", ignore = true)
	CarrelloDTO toDto(Carrello carrello);
}
