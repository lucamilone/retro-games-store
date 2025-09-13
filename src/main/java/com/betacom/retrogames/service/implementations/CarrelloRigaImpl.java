package com.betacom.retrogames.service.implementations;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.betacom.retrogames.dto.CarrelloRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.CarrelloRigaMapper;
import com.betacom.retrogames.model.CarrelloRiga;
import com.betacom.retrogames.repository.CarrelloRepository;
import com.betacom.retrogames.repository.CarrelloRigaRepository;
import com.betacom.retrogames.repository.ProdottoRepository;
import com.betacom.retrogames.request.CarrelloRigaReq;
import com.betacom.retrogames.service.interfaces.CarrelloRigaService;
import com.betacom.retrogames.service.interfaces.MessaggioService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
@Service
public class CarrelloRigaImpl implements CarrelloRigaService {
	private final CarrelloRigaRepository carrelloRigaRepo;
	private final CarrelloRepository carrelloRepo;
	private final ProdottoRepository prodottoRepo;
	private final CarrelloRigaMapper carrelloRigaMapper;
	private final MessaggioService msgS;
	

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer aggiungiProdotto(CarrelloRigaReq req) throws AcademyException {
		log.debug("Aggiungi prodotto al carrello: {}", req);
		CarrelloRiga carrelloRiga = new CarrelloRiga();
		carrelloRiga.setCarrello(carrelloRepo.findById(req.getCarrelloId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("carrello-non-trovato"))));
		carrelloRiga.setProdotto(prodottoRepo.findById(req.getProdottoId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-trovato"))));
		carrelloRiga.setQuantita(req.getQuantita());
		return carrelloRigaRepo.save(carrelloRiga).getId();
		
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiornaRiga(CarrelloRigaReq req) throws AcademyException {
		log.debug("Aggiorna riga carrello: {}", req);
		CarrelloRiga carrelloRiga = carrelloRigaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("riga-carrello-non-trovata")));
		if(req.getCarrelloId() != null) {
			carrelloRiga.setCarrello(carrelloRepo.findById(req.getCarrelloId())
					.orElseThrow(() -> new AcademyException(msgS.getMessaggio("carrello-non-trovato"))));
		}
		if(req.getProdottoId() != null) {
			carrelloRiga.setProdotto(prodottoRepo.findById(req.getProdottoId())
					.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-trovato"))));
		}
		if(req.getQuantita() != null)
			carrelloRiga.setQuantita(req.getQuantita());
		carrelloRigaRepo.save(carrelloRiga);
		
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void rimuoviProdotto(CarrelloRigaReq req) throws AcademyException {
		log.debug("Rimuovi prodotto dal carrello: {}", req);
		CarrelloRiga carrelloRiga = carrelloRigaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("riga-carrello-non-trovata")));
		carrelloRigaRepo.delete(carrelloRiga);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public List<CarrelloRigaDTO> listByCarrello(Integer carrelloId) throws AcademyException {
		log.debug("Recupera righe carrello per carrelloId: {}", carrelloId);
		
		List<CarrelloRiga> righe = carrelloRigaRepo.findAllByCarrelloId(carrelloId);
		List<CarrelloRigaDTO> dtos = carrelloRigaMapper.toDtoList(righe);
		for(CarrelloRigaDTO dto: dtos) {
			dto.setSubTotale(dto.getProdotto().getPrezzo().multiply(BigDecimal.valueOf(dto.getQuantita())));
		}
		return dtos;
	}

}
