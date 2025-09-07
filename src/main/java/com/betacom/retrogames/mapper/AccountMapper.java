package com.betacom.retrogames.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.model.Account;

@Mapper(componentModel = "spring", uses = { IndirizzoMapper.class, MetodoPagamentoMapper.class, RuoloMapper.class,
		CredenzialeMapper.class, CarrelloMapper.class })
public interface AccountMapper {

	@Mapping(target = "ruolo", source = "ruolo.nome")
	AccountDTO toDto(Account account);

	List<AccountDTO> toDtoList(List<Account> accounts);
}
