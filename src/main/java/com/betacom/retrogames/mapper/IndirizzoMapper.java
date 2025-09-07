package com.betacom.retrogames.mapper;

import org.mapstruct.Mapper;

import com.betacom.retrogames.dto.IndirizzoDTO;
import com.betacom.retrogames.model.Indirizzo;

@Mapper(componentModel = "spring")
public interface IndirizzoMapper {

	IndirizzoDTO toDto(Indirizzo indirizzo);
}
