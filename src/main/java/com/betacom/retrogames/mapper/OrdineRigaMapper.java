package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.model.OrdineRiga;

@Mapper(componentModel = "spring", uses = { ProdottoMapper.class })
public interface OrdineRigaMapper {

	@Mapping(target = "ordineId", source = "ordine.id")
	OrdineRigaDTO toDto(OrdineRiga riga);

	List<OrdineRigaDTO> toDtoList(List<OrdineRiga> righe);
}
