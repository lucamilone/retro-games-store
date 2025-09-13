package com.betacom.retrogames.service.implementations;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.betacom.retrogames.dto.CarrelloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.CarrelloMapper;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.Carrello;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.CarrelloRepository;
import com.betacom.retrogames.request.CarrelloReq;
import com.betacom.retrogames.service.interfaces.CarrelloService;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;


@AllArgsConstructor
@Log4j2
@Service
public class CarrelloImpl implements CarrelloService{
	private final AccountRepository accountRepo;
	private final CarrelloRepository carrelloRepo;
	private final CarrelloMapper carrelloMapper;
	private final MessaggioService msgS;

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer creaPerAccount(CarrelloReq req) throws AcademyException {
		log.debug("Crea carrello per account: {}", req);
		
		Account account = accountRepo.findById(req.getAccountId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-esistente")));
		
		Carrello carrello = new Carrello();
		carrello.setAccount(account);
		return carrelloRepo.save(carrello).getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void svuotaCarrello(CarrelloReq req) throws AcademyException {
		log.debug("Svuota carrello: {}", req);
		Carrello carrello = carrelloRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("carrello-non-esistente")));
		carrello.getRighe().clear();
		carrelloRepo.save(carrello);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public CarrelloDTO getCarrelloByAccount(Integer accountId) throws AcademyException {
		log.debug("Recupera carrello per account: {}", accountId);
		Carrello carrello = carrelloRepo.findByAccountId(accountId)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("carrello-non-esistente-per-account")));
		CarrelloDTO dto = carrelloMapper.toDto(carrello);
		dto.setTotale(carrello.getTotale());
		dto.setTotaleQuantita(carrello.getTotaleQuantita());
		return dto;
	}

}
