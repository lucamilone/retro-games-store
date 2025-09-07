package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.MetodoPagamentoDTO;
import com.betacom.retrogames.model.MetodoPagamento;

@Mapper(componentModel = "spring", uses = { TipoMetodoPagamentoMapper.class })
public interface MetodoPagamentoMapper {

	@Mapping(target = "accountId", source = "account.id")
	MetodoPagamentoDTO toDto(MetodoPagamento metodoPagamento);

	List<MetodoPagamentoDTO> toDtoList(List<MetodoPagamento> metodiPagamento);
}
