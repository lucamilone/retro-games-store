package com.betacom.retrogames.service.implementations;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.CarrelloRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.CarrelloRigaMapper;
import com.betacom.retrogames.model.Carrello;
import com.betacom.retrogames.model.CarrelloRiga;
import com.betacom.retrogames.model.Prodotto;
import com.betacom.retrogames.repository.CarrelloRepository;
import com.betacom.retrogames.repository.CarrelloRigaRepository;
import com.betacom.retrogames.repository.ProdottoRepository;
import com.betacom.retrogames.request.CarrelloRigaReq;
import com.betacom.retrogames.service.interfaces.CarrelloRigaService;
import com.betacom.retrogames.service.interfaces.MessaggioService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CarrelloRigaImpl implements CarrelloRigaService {
	private final CarrelloRigaRepository carrelloRigaRepo;
	private final CarrelloRepository carrelloRepo;
	private final ProdottoRepository prodottoRepo;
	private final MessaggioService msgS;
	private final CarrelloRigaMapper carrelloRigaMapper;

	public CarrelloRigaImpl(CarrelloRigaRepository carrelloRigaRepo, CarrelloRepository carrelloRepo,
			ProdottoRepository prodottoRepo, MessaggioService msgS, CarrelloRigaMapper carrelloRigaMapper) {
		this.carrelloRigaRepo = carrelloRigaRepo;
		this.carrelloRepo = carrelloRepo;
		this.prodottoRepo = prodottoRepo;
		this.msgS = msgS;
		this.carrelloRigaMapper = carrelloRigaMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiungiProdotto(CarrelloRigaReq req) throws AcademyException {
		log.debug("AggiungiProdotto: {}", req);

		// Verifico l'esistenza del carrello
		Carrello carrello = carrelloRepo.findById(req.getCarrelloId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("carrello-non-trovato")));

		// Verifico l'esistenza del prodotto
		Prodotto prodotto = prodottoRepo.findById(req.getProdottoId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-trovato")));

		// Verifico l'esistenza di una riga per carrello + prodotto
		Optional<CarrelloRiga> rigaOpt = carrelloRigaRepo.findByCarrelloIdAndProdottoId(req.getCarrelloId(),
				req.getProdottoId());

		if (rigaOpt.isPresent()) {
			CarrelloRiga riga = rigaOpt.get();
			riga.setQuantita(riga.getQuantita() + req.getQuantita());
			carrelloRigaRepo.save(riga);

			log.debug("Quantità aggiornata per riga esistente: ProdottoId = {}, NuovaQuantità = {}",
					riga.getProdotto().getId(), riga.getQuantita());

			return; // Esce per evitare di creare una nuova riga
		}

		// Nuova riga
		CarrelloRiga newRiga = new CarrelloRiga();
		newRiga.setProdotto(prodotto);
		newRiga.setQuantita(req.getQuantita());

		// Manteniene coerenza: aggiunge tramite l'aggregate root Carrello
		carrello.addRiga(newRiga);

		// Salvo il carrello (cascade salva la riga)
		carrelloRepo.save(carrello);

		log.debug("Riga carrello creata con successo: ProdottoId = {}, Quantità = {}", prodotto.getId(),
				req.getQuantita());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiornaRiga(CarrelloRigaReq req) throws AcademyException {
		log.debug("AggiornaRiga: {}", req);

		// Verifico l'esistenza della riga del carrello
		CarrelloRiga riga = carrelloRigaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("riga-carrello-non-trovata")));

		// Se cambio prodotto: verifica l'esistenza della riga per lo stesso carrello + nuovo prodotto
		if (req.getProdottoId() != null && !req.getProdottoId().equals(riga.getProdotto().getId())) {
			// Verifico l'esistenza di una riga per carrello + nuovo prodotto
			Optional<CarrelloRiga> rigaOpt = carrelloRigaRepo.findByCarrelloIdAndProdottoId(riga.getCarrello().getId(),
					req.getProdottoId());

			if (rigaOpt.isPresent()) {
				// Merge quantità
				CarrelloRiga current = rigaOpt.get();

				int nuovaQuantita = current.getQuantita()
						+ (req.getQuantita() != null ? req.getQuantita() : riga.getQuantita());
				current.setQuantita(nuovaQuantita);

				carrelloRigaRepo.save(current);
				carrelloRigaRepo.delete(riga);

				log.debug("Merge righe carrello completato: unito {} su {}", riga.getId(), current.getId());

				return; // Esce per evitare salvataggi doppi: la riga originale è stata eliminata
			} else {
				// Verifico l'esistenza del prodotto
				Prodotto nuovoProdotto = prodottoRepo.findById(req.getProdottoId())
						.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-trovato")));

				// Aggiorno solo il prodotto della riga
				riga.setProdotto(nuovoProdotto);
			}
		}

		if (req.getQuantita() != null) {
			riga.setQuantita(req.getQuantita());
			if (riga.getQuantita() <= 0) {
				// Rimuovo se quantità <= 0
				carrelloRigaRepo.delete(riga);

				log.debug("Riga carrello eliminata perché quantità <= 0. ID: {}", req.getId());

				return;
			}
		}

		// Salvo la riga del carrello aggiornata
		carrelloRigaRepo.save(riga);

		log.debug("Riga carrello aggiornata con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void rimuoviProdotto(CarrelloRigaReq req) throws AcademyException {
		log.debug("RimuoviProdotto: {}", req);

		// Verifico l'esistenza della riga del carrello
		CarrelloRiga riga = carrelloRigaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("riga-carrello-non-trovata")));

		Carrello carrello = riga.getCarrello();

		// Manteniene coerenza: rimuove tramite l'aggregate root Carrello
		carrello.removeRiga(riga);

		// Salvo il carrello aggiornato
		carrelloRepo.save(carrello);

		log.debug("Riga carrello rimossa con successo. ID: {}", req.getId());
	}

	@Override
	public List<CarrelloRigaDTO> listByCarrello(Integer carrelloId) throws AcademyException {
		log.debug("ListByCarrello: {}", carrelloId);

		// Recupero le righe del carrello
		List<CarrelloRiga> righe = carrelloRigaRepo.findAllByCarrelloId(carrelloId);

		// Mappo Entity a DTO
		List<CarrelloRigaDTO> righeDtos = carrelloRigaMapper.toDtoList(righe);

		// Calcolo il SubTotale dinamicamente da esporre verso il frontend
		Iterator<CarrelloRiga> it = righe.iterator();
		for (CarrelloRigaDTO riga : righeDtos) {
			riga.setSubTotale(it.next().getSubTotale());
		}

		return righeDtos;
	}
}
