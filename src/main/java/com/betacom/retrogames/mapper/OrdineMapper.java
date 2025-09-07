package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.OrdineDTO;
import com.betacom.retrogames.model.Ordine;

@Mapper(componentModel = "spring", uses = { OrdineRigaMapper.class, IndirizzoMapper.class, PagamentoMapper.class })
public interface OrdineMapper {

	@Mapping(target = "accountId", source = "account.id")
	@Mapping(target = "totaleQuantita", ignore = true)
	OrdineDTO toDto(Ordine ordine);

	List<OrdineDTO> toDtoList(List<Ordine> ordini);
}
