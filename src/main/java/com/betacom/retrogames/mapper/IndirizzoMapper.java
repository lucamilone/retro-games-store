package com.betacom.retrogames.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.betacom.retrogames.dto.IndirizzoDTO;
import com.betacom.retrogames.model.Indirizzo;
import com.betacom.retrogames.request.IndirizzoReq;

@Mapper(componentModel = "spring")
public interface IndirizzoMapper {

	IndirizzoDTO toDto(Indirizzo indirizzo);

	Indirizzo toEntity(IndirizzoReq req);

	// Aggiorna solo i campi non Null
	void updateIndirizzoFromReq(IndirizzoReq req, @MappingTarget Indirizzo indirizzo);
}
