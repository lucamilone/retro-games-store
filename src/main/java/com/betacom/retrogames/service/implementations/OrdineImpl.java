package com.betacom.retrogames.service.implementations;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.OrdineDTO;
import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.IndirizzoMapper;
import com.betacom.retrogames.mapper.OrdineMapper;
import com.betacom.retrogames.mapper.PagamentoMapper;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.Carrello;
import com.betacom.retrogames.model.CarrelloRiga;
import com.betacom.retrogames.model.Ordine;
import com.betacom.retrogames.model.OrdineRiga;
import com.betacom.retrogames.model.Pagamento;
import com.betacom.retrogames.model.enums.StatoOrdine;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.OrdineRepository;
import com.betacom.retrogames.request.CarrelloReq;
import com.betacom.retrogames.request.OrdineReq;
import com.betacom.retrogames.service.interfaces.CarrelloService;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.OrdineRigaService;
import com.betacom.retrogames.service.interfaces.OrdineService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrdineImpl implements OrdineService {
	private final OrdineRepository ordineRepo;
	private final AccountRepository accountRepo;
	private final MessaggioService msgS;
	private final CarrelloService carrelloS;
	private final OrdineRigaService ordineRigaS;
	private final OrdineMapper ordineMapper;
	private final IndirizzoMapper indirizzoMapper;
	private final PagamentoMapper pagamentoMapper;

	public OrdineImpl(OrdineRepository ordineRepo, AccountRepository accountRepo, MessaggioService msgS,
			CarrelloService carrelloS, OrdineRigaService ordineRigaS, OrdineMapper ordineMapper,
			IndirizzoMapper indirizzoMapper, PagamentoMapper pagamentoMapper) {
		this.ordineRepo = ordineRepo;
		this.accountRepo = accountRepo;
		this.msgS = msgS;
		this.carrelloS = carrelloS;
		this.ordineRigaS = ordineRigaS;
		this.ordineMapper = ordineMapper;
		this.indirizzoMapper = indirizzoMapper;
		this.pagamentoMapper = pagamentoMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public OrdineDTO crea(OrdineReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico l'esistenza dell'account
		Account account = accountRepo.findById(req.getAccountId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-trovato")));

		// Recupero carrello dell'account
		Carrello carrello = account.getCarrello();
		if (carrello == null || carrello.getRighe().isEmpty()) {
			throw new AcademyException(msgS.getMessaggio("carrello-vuoto"));
		}

		Ordine ordine = new Ordine();
		ordine.setAccount(account);

		if (req.getIndirizzoSpedizione() != null) {
			ordine.setIndirizzoSpedizione(indirizzoMapper.toEntity(req.getIndirizzoSpedizione()));
		} else {
			ordine.setIndirizzoSpedizione(account.getIndirizzo());
		}

		// Trasferisco righe dal carrello all'ordine
		for (CarrelloRiga carrelloRiga : carrello.getRighe()) {
			OrdineRiga ordineRiga = new OrdineRiga();
			ordineRiga.setProdotto(carrelloRiga.getProdotto());
			ordineRiga.setQuantita(carrelloRiga.getQuantita());
			ordineRiga.setPrezzoUnitario(carrelloRiga.getProdotto().getPrezzo());

			ordine.addRiga(ordineRiga);
		}

		BigDecimal totale = ordine.getTotale();
		ordine.setTotale(totale);

		// Salvo l'ordine
		ordine = ordineRepo.save(ordine);

		log.debug("Ordine creato con successo. ID: {}", ordine.getId());

		// Svuoto il carrello
		CarrelloReq carrelloReq = new CarrelloReq();
		carrelloReq.setId(carrello.getId());
		carrelloS.svuotaCarrello(carrelloReq);

		return ordineMapper.toDto(ordine);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(OrdineReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Verifico l'esistenza dell'ordine
		Ordine ordine = ordineRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-trovato")));

		// Permetto aggiornamenti solo se l'ordine è ancora in stato iniziale
		if (!StatoOrdine.IN_ATTESA.equals(ordine.getStatoOrdine())) {
			throw new AcademyException(msgS.getMessaggio("ordine-non-modificabile"));
		}

		if (req.getIndirizzoSpedizione() != null) {
			indirizzoMapper.updateIndirizzoFromReq(req.getIndirizzoSpedizione(), ordine.getIndirizzoSpedizione());
		}

		if (req.getPagamento() != null) {
			if (ordine.getPagamento() == null) {
				Pagamento pagamento = pagamentoMapper.toEntity(req.getPagamento());
				ordine.setPagamentoWithRelation(pagamento);
			} else {
				pagamentoMapper.updatePagamentoFromReq(req.getPagamento(), ordine.getPagamento());
			}
		}

		// Salvo l'ordine aggiornato
		ordineRepo.save(ordine);

		log.debug("Ordine aggiornato con successo. ID: {}", ordine.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiornaStato(OrdineReq req) throws AcademyException {
		log.debug("AggiornaStato: ID = {}, NuovoStato = {}", req.getId(), req.getStatoOrdine());

		// Verifico l'esistenza dell'ordine
		Ordine ordine = ordineRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-trovato")));

		StatoOrdine statoRichiesto = req.getStatoOrdine();
		StatoOrdine statoCorrente = ordine.getStatoOrdine();

		// Controllo la validità della transizione usando l'helper dell'enum
		if (!statoCorrente.isTransizioneValidaVerso(statoRichiesto)) {
			throw new AcademyException(msgS.getMessaggio("transizione-stato-non-valida"));
		}

		// Aggiorno lo stato
		ordine.setStatoOrdine(statoRichiesto);
		log.debug("Transizione stato ordine: {} -> {}", statoCorrente, statoRichiesto);

		// Salvo l'ordine aggiornato
		ordineRepo.save(ordine);

		log.debug("Stato ordine aggiornato con successo. ID: {}, Nuovo Stato: {}", req.getId(), statoRichiesto);
	}

	@Override
	public OrdineDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Verifico l'esistenza dell'ordine
		Ordine ordine = ordineRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-trovato")));

		OrdineDTO dto = ordineMapper.toDto(ordine);
		dto.setTotaleQuantita(ordine.getTotaleQuantita());

		return dto;
	}

	@Override
	public List<OrdineDTO> listByAccount(Integer accountId) {
		log.debug("ListByAccount: {}", accountId);

		// Recupero gli ordini dell'account
		List<Ordine> ordini = ordineRepo.findAllByAccountId(accountId);

		// Mappo Entity a DTO
		return ordini.stream().map(ordine -> {
			List<OrdineRigaDTO> righeDto = ordineRigaS.listByOrdine(ordine.getId());

			OrdineDTO dto = ordineMapper.toDto(ordine);
			dto.setRighe(righeDto);
			dto.setTotaleQuantita(ordine.getTotaleQuantita());

			return dto;
		}).toList();
	}
}
