package com.betacom.retrogames.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.PagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.PagamentoMapper;
import com.betacom.retrogames.model.MetodoPagamento;
import com.betacom.retrogames.model.Ordine;
import com.betacom.retrogames.model.Pagamento;
import com.betacom.retrogames.model.enums.StatoPagamento;
import com.betacom.retrogames.repository.MetodoPagamentoRepository;
import com.betacom.retrogames.repository.OrdineRepository;
import com.betacom.retrogames.repository.PagamentoRepository;
import com.betacom.retrogames.request.PagamentoReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.PagamentoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PagamentoImpl implements PagamentoService {
	private final PagamentoRepository pagamentoRepo;
	private final MetodoPagamentoRepository metodoPagamentoRepo;
	private final OrdineRepository ordineRepo;
	private final MessaggioService msgS;
	private final PagamentoMapper pagamentoMapper;

	public PagamentoImpl(PagamentoRepository pagamentoRepo, MetodoPagamentoRepository metodoPagamentoRepo,
			OrdineRepository ordineRepo, MessaggioService msgS, PagamentoMapper pagamentoMapper) {
		this.pagamentoRepo = pagamentoRepo;
		this.metodoPagamentoRepo = metodoPagamentoRepo;
		this.ordineRepo = ordineRepo;
		this.msgS = msgS;
		this.pagamentoMapper = pagamentoMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public PagamentoDTO crea(PagamentoReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico l'esistenza dell'ordine
		Ordine ordine = ordineRepo.findById(req.getOrdineId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-trovato")));

		// Verifico l'esistenza di un pagamento per lo stesso ordine
		if (pagamentoRepo.findByOrdineId(req.getOrdineId()).isPresent()) {
			throw new AcademyException(msgS.getMessaggio("pagamento-esistente"));
		}

		// Recupero dal DB per salvare i dati
		MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(req.getMetodoPagamentoId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-trovato")));

		Pagamento pagamento = new Pagamento();
		pagamento.setOrdine(ordine);
		pagamento.setTotale(ordine.getTotale());
		pagamento.setMetodoPagamento(metodoPagamento);

		// Salvo il pagamento 
		pagamento = pagamentoRepo.save(pagamento);

		log.debug("Pagamento creato con successo. ID: {}", pagamento.getId());

		return pagamentoMapper.toDto(pagamento);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(PagamentoReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Verifico l'esistenza del pagamento
		Pagamento pagamento = pagamentoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("pagamento-non-trovato")));

		// Permetto aggiornamenti solo se il pagamento è ancora in stato iniziale
		if (!StatoPagamento.IN_ATTESA.equals(pagamento.getStatoPagamento())) {
			throw new AcademyException(msgS.getMessaggio("pagamento-non-modificabile"));
		}

		if (req.getMetodoPagamentoId() != null) {
			MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(req.getMetodoPagamentoId())
					.orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-trovato")));

			pagamento.setMetodoPagamento(metodoPagamento);
		}

		if (req.getTotale() != null) {
			pagamento.setTotale(req.getTotale());
		}

		// Salvo il pagamento aggiornato
		pagamentoRepo.save(pagamento);

		log.debug("Pagamento aggiornato con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiornaStato(PagamentoReq req) throws AcademyException {
		log.debug("AggiornaStato: ID = {}, NuovoStato = {}", req.getId(), req.getStatoPagamento());

		// Verifico l'esistenza del pagamento
		Pagamento pagamento = pagamentoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("pagamento-non-trovato")));

		StatoPagamento statoRichiesto = req.getStatoPagamento();
		StatoPagamento statoCorrente = pagamento.getStatoPagamento();

		// Controllo la validità della transizione usando l'helper dell'enum
		if (!statoCorrente.isTransizioneValidaVerso(statoRichiesto)) {
			throw new AcademyException(msgS.getMessaggio("transizione-stato-non-valida"));
		}

		// Aggiorno lo stato
		pagamento.setStatoPagamento(statoRichiesto);
		log.debug("Transizione stato pagamento: {} -> {}", statoCorrente, statoRichiesto);

		// Salvo il pagamento aggiornato
		pagamentoRepo.save(pagamento);

		log.debug("Stato pagamento aggiornato con successo. ID: {}, Nuovo Stato: {}", req.getId(), statoRichiesto);
	}

	@Override
	public PagamentoDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Recupero il pagamento dal DB
		Pagamento pagamento = pagamentoRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("pagamento-non-trovato")));

		return pagamentoMapper.toDto(pagamento);
	}

	@Override
	public List<PagamentoDTO> list() {
		log.debug("List");

		// Recupero tutti i pagamenti
		List<Pagamento> pagamenti = pagamentoRepo.findAll();

		return pagamentoMapper.toDtoList(pagamenti);
	}
}