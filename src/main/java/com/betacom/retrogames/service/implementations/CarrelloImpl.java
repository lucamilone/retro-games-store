package com.betacom.retrogames.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.CarrelloDTO;
import com.betacom.retrogames.dto.CarrelloRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.CarrelloMapper;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.Carrello;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.CarrelloRepository;
import com.betacom.retrogames.request.CarrelloReq;
import com.betacom.retrogames.service.interfaces.CarrelloRigaService;
import com.betacom.retrogames.service.interfaces.CarrelloService;
import com.betacom.retrogames.service.interfaces.MessaggioService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CarrelloImpl implements CarrelloService {
	private final AccountRepository accountRepo;
	private final CarrelloRepository carrelloRepo;
	private final MessaggioService msgS;
	private final CarrelloRigaService carrelloRigaS;
	private final CarrelloMapper carrelloMapper;

	public CarrelloImpl(AccountRepository accountRepo, CarrelloRepository carrelloRepo, MessaggioService msgS,
			CarrelloRigaService carrelloRigaS, CarrelloMapper carrelloMapper) {
		this.accountRepo = accountRepo;
		this.carrelloRepo = carrelloRepo;
		this.msgS = msgS;
		this.carrelloRigaS = carrelloRigaS;
		this.carrelloMapper = carrelloMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer creaPerAccount(CarrelloReq req) throws AcademyException {
		log.debug("CreaPerAccount: {}", req);

		// Verifico l'esistenza dell'account
		Account account = accountRepo.findById(req.getAccountId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-trovato")));

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		// Salvo il carrello
		carrello = carrelloRepo.save(carrello);

		log.debug("Carrello creato con successo. ID: {}", carrello.getId());

		// Restituisco l'id generato
		return carrello.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void svuotaCarrello(CarrelloReq req) throws AcademyException {
		log.debug("SvuotaCarrello: {}", req);

		// Verifico l'esistenza del carrello
		Carrello carrello = carrelloRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("carrello-non-trovato")));

		// Svuoto il carrello
		carrello.getRighe().clear();

		// Salvo il carrello aggiornato
		carrelloRepo.save(carrello);

		log.debug("Carrello svuotato con successo. ID: {}", req.getId());
	}

	@Override
	public CarrelloDTO getCarrelloByAccount(Integer accountId) throws AcademyException {
		log.debug("GetCarrelloByAccount: {}", accountId);

		// Verifico l'esistenza del carrello
		Carrello carrello = carrelloRepo.findByAccountId(accountId)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("carrello-non-trovato-per-account")));

		List<CarrelloRigaDTO> righeDto = carrelloRigaS.listByCarrello(carrello.getId());

		CarrelloDTO dto = carrelloMapper.toDto(carrello);
		dto.setRighe(righeDto);
		dto.setTotaleQuantita(carrello.getTotaleQuantita());
		dto.setTotale(carrello.getTotale());

		return dto;
	}
}
