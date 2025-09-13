package com.betacom.retrogames.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.AccountMapper;
import com.betacom.retrogames.mapper.IndirizzoMapper;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.Credenziale;
import com.betacom.retrogames.model.Indirizzo;
import com.betacom.retrogames.model.Ruolo;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.CredenzialeRepository;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.AccountReq;
import com.betacom.retrogames.request.CarrelloReq;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.service.interfaces.AccountService;
import com.betacom.retrogames.service.interfaces.CarrelloService;
import com.betacom.retrogames.service.interfaces.CredenzialeService;
import com.betacom.retrogames.service.interfaces.MessaggioService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AccountImpl implements AccountService {
	private final CacheManager cacheManager;
	private final AccountRepository accountRepo;
	private final CredenzialeRepository credenzialeRepo;
	private final RuoloRepository ruoloRepo;
	private final MessaggioService msgS;
	private final CredenzialeService credenzialeS;
	private final CarrelloService carrelloS;
	private final AccountMapper accountMapper;
	private final IndirizzoMapper indirizzoMapper;

	public AccountImpl(CacheManager cacheManager, AccountRepository accountRepo, CredenzialeRepository credenzialeRepo,
			RuoloRepository ruoloRepo, MessaggioService msgS, CredenzialeService credenzialeS,
			CarrelloService carrelloS, AccountMapper accountMapper, IndirizzoMapper indirizzoMapper) {
		this.cacheManager = cacheManager;
		this.accountRepo = accountRepo;
		this.credenzialeRepo = credenzialeRepo;
		this.ruoloRepo = ruoloRepo;
		this.msgS = msgS;
		this.credenzialeS = credenzialeS;
		this.carrelloS = carrelloS;
		this.accountMapper = accountMapper;
		this.indirizzoMapper = indirizzoMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer crea(AccountReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		Credenziale credenziale = credenzialeRepo.findById(req.getCredenzialeId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("credenziale-non-trovata")));

		Ruolo ruolo = getRuoloFromReq(req.getRuoloId());

		Account account = new Account();
		Indirizzo indirizzo = indirizzoMapper.toEntity(req.getIndirizzo());
		account.setNome(req.getNome());
		account.setCognome(req.getCognome());
		account.setIndirizzo(indirizzo);
		account.setCredenziale(credenziale);
		account.setRuolo(ruolo);
		account.setAttivo(true);

		// Salvo l'account
		account = accountRepo.save(account);

		// Creo il carrello associato all'account
		CarrelloReq carrelloReq = new CarrelloReq();
		carrelloReq.setAccountId(account.getId());
		carrelloS.creaPerAccount(carrelloReq);

		log.debug("Account creato con successo. ID: {}", account.getId());

		// Restituisco l'id generato
		return account.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(AccountReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Verifico l'esistenza dell'account
		Account account = accountRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-trovato")));

		if (req.getNome() != null) {
			account.setNome(req.getNome());
		}

		if (req.getCognome() != null) {
			account.setCognome(req.getCognome());
		}

		if (req.getIndirizzo() != null) {
			indirizzoMapper.updateIndirizzoFromReq(req.getIndirizzo(), account.getIndirizzo());
		}

		if (req.getRuoloId() != null) {
			Ruolo ruolo = getRuoloFromReq(req.getRuoloId());
			account.setRuolo(ruolo);
		}

		// Salvo l'account aggiornato
		accountRepo.save(account);

		log.debug("Account aggiornato con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(AccountReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Verifico l'esistenza dell'account
		Account account = accountRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-trovato")));

		// Disattivo l'account
		account.setAttivo(false);

		// Disattivo credenziale tramite il service dedicato
		CredenzialeReq credenzialeReq = new CredenzialeReq();
		credenzialeReq.setId(account.getCredenziale().getId());
		credenzialeS.disattiva(credenzialeReq);

		// Salvo l'account disattivato
		accountRepo.save(account);

		log.debug("Account disattivato con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void riattiva(AccountReq req) throws AcademyException {
		log.debug("Riattiva: {}", req);

		// Verifico l'esistenza dell'account
		Account account = accountRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-trovato")));

		// Riattivo l'account
		account.setAttivo(true);

		// Riattivo credenziale tramite il service dedicato
		CredenzialeReq credenzialeReq = new CredenzialeReq();
		credenzialeReq.setId(account.getCredenziale().getId());
		credenzialeS.riattiva(credenzialeReq);

		// Salvo l'account riattivato
		accountRepo.save(account);

		log.debug("Account riattivato con successo. ID: {}", req.getId());
	}

	@Override
	public AccountDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Recupero l'account dal DB
		Account account = accountRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-trovato")));

		return accountMapper.toDto(account);
	}

	@Override
	public List<AccountDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo gli account attivi
		List<Account> accountAttivi = accountRepo.findByAttivoTrue();

		return accountMapper.toDtoList(accountAttivi);
	}

	private Ruolo getRuoloFromReq(Integer ruoloId) throws AcademyException {
		// Controllo se il ruolo è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.RUOLO, ruoloId)) {
			throw new AcademyException(msgS.getMessaggio("ruolo-non-trovato"));
		}

		// Recupero dal DB per salvare i dati
		return ruoloRepo.findById(ruoloId)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ruolo-non-trovato")));
	}
}
