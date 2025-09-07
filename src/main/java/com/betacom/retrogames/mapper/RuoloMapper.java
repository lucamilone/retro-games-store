package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.model.Ruolo;

@Mapper(componentModel = "spring")
public interface RuoloMapper {

	RuoloDTO toDto(Ruolo ruolo);

	List<RuoloDTO> toDtos(List<RuoloDTO> ruoli);
}
