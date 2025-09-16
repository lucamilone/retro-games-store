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
import com.betacom.retrogames.repository.MetodoPagamentoRepository;
import com.betacom.retrogames.repository.OrdineRepository;
import com.betacom.retrogames.repository.PagamentoRepository;
import com.betacom.retrogames.request.PagamentoReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.PagamentoService;

import lombok.extern.log4j.Log4j2;

@Log4j2
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
	public Integer crea(PagamentoReq req) throws AcademyException {
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
		pagamento.setTotale(req.getTotale());
		pagamento.setMetodoPagamento(metodoPagamento);

		// Salvo il pagamento 
		pagamento = pagamentoRepo.save(pagamento);

		log.debug("Pagamento creato con successo. ID: {}", pagamento.getId());

		return pagamento.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(PagamentoReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Verifico l'esistenza del pagamento
		Pagamento pagamento = pagamentoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("pagamento-non-trovato")));

		if (req.getStatoPagamento() != null) {
			pagamento.setStatoPagamento(req.getStatoPagamento());
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