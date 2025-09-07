package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.model.Credenziale;

@Mapper(componentModel = "spring")
public interface CredenzialeMapper {

	@Mapping(target = "accountId", source = "account.id")
	CredenzialeDTO toDto(Credenziale credenziale);

	List<CredenzialeDTO> toDtoList(List<Credenziale> credenziali);
}
