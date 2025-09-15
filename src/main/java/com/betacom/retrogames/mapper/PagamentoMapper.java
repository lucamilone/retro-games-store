package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.betacom.retrogames.dto.PagamentoDTO;
import com.betacom.retrogames.model.MetodoPagamento;
import com.betacom.retrogames.model.Ordine;
import com.betacom.retrogames.model.Pagamento;
import com.betacom.retrogames.request.PagamentoReq;

@Mapper(componentModel = "spring", uses = { MetodoPagamentoMapper.class })
public interface PagamentoMapper {

	@Mapping(target = "ordineId", source = "ordine.id")
	PagamentoDTO toDto(Pagamento pagamento);

	@Mapping(target = "ordine", source = "ordineId", qualifiedByName = "ordineFromId")
	@Mapping(target = "metodoPagamento", source = "metodoPagamentoId", qualifiedByName = "metodoPagamentoFromId")
	@Mapping(target = "creatoIl", ignore = true)
	@Mapping(target = "aggiornatoIl", ignore = true)
	Pagamento toEntity(PagamentoReq req);

	// Aggiorna solo i campi non Null
	@Mapping(target = "ordine", source = "ordineId", qualifiedByName = "ordineFromId")
	@Mapping(target = "metodoPagamento", source = "metodoPagamentoId", qualifiedByName = "metodoPagamentoFromId")
	@Mapping(target = "creatoIl", ignore = true)
	@Mapping(target = "aggiornatoIl", ignore = true)
	void updatePagamentoFromReq(PagamentoReq req, @MappingTarget Pagamento indirizzoSpedizione);

	List<PagamentoDTO> toDtoList(List<Pagamento> pagamenti);

	// Metodi helper per convertire ID in Entity stub
	@Named("ordineFromId")
	public static Ordine mapOrdine(Integer id) {
		if (id == null) {
			return null;
		}
		Ordine ordine = new Ordine();
		ordine.setId(id);
		return ordine;
	}

	@Named("metodoPagamentoFromId")
	public static MetodoPagamento mapMetodoPagamento(Integer id) {
		if (id == null) {
			return null;
		}
		MetodoPagamento metodo = new MetodoPagamento();
		metodo.setId(id);
		return metodo;
	}
}
