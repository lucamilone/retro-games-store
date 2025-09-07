package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.model.TipoMetodoPagamento;

@Mapper(componentModel = "spring")
public interface TipoMetodoPagamentoMapper {

	TipoMetodoPagamentoDTO toDto(TipoMetodoPagamento tipo);

	List<TipoMetodoPagamentoDTO> toDtos(List<TipoMetodoPagamentoDTO> tipi);
}
