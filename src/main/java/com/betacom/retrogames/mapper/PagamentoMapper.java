package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.PagamentoDTO;
import com.betacom.retrogames.model.Pagamento;

@Mapper(componentModel = "spring", uses = { MetodoPagamentoMapper.class })
public interface PagamentoMapper {

	@Mapping(target = "ordineId", source = "ordine.id")
	PagamentoDTO toDto(Pagamento pagamento);

	List<PagamentoDTO> toDtos(List<PagamentoDTO> pagamenti);
}
