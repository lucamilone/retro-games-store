package com.betacom.retrogames.service.implementations;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.OrdineRigaMapper;
import com.betacom.retrogames.model.OrdineRiga;
import com.betacom.retrogames.repository.OrdineRepository;
import com.betacom.retrogames.repository.OrdineRigaRepository;
import com.betacom.retrogames.repository.ProdottoRepository;
import com.betacom.retrogames.request.OrdineRigaReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.OrdineRigaService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
@Service
public class OrdineRigaImpl implements OrdineRigaService {
	final OrdineRepository ordineRepo;
	final ProdottoRepository prodottoRepo;
	final OrdineRigaRepository ordineRigaRepo;
	final OrdineRigaMapper ordineRigaMapper;
	final MessaggioService msgS;

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer aggiungiProdotto(OrdineRigaReq req) throws AcademyException {
		log.debug("Aggiungi prodotto all'ordine: {}", req);
		OrdineRiga ordineRiga = new OrdineRiga();
		ordineRiga.setOrdine(ordineRepo.findById(req.getOrdineId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-esistente"))));
		ordineRiga.setProdotto(prodottoRepo.findById(req.getProdottoId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-esistente"))));
		ordineRiga.setQuantita(req.getQuantita());
		ordineRiga.setPrezzoUnitario(req.getPrezzoUnitario());
		return ordineRigaRepo.save(ordineRiga).getId();
		
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiornaRiga(OrdineRigaReq req) throws AcademyException {
		log.debug("Aggiorna riga ordine: {}", req);
		
		OrdineRiga ordineRiga = ordineRigaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("riga-ordine-non-esistente")));
		if(req.getOrdineId() != null) {
			ordineRiga.setOrdine(ordineRepo.findById(req.getOrdineId())
					.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-esistente"))));
		}
		if(req.getProdottoId() != null) {
			ordineRiga.setProdotto(prodottoRepo.findById(req.getProdottoId())
					.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-esistente"))));
		}
		if(req.getQuantita() != null) {
			ordineRiga.setQuantita(req.getQuantita());
		}
		if(req.getPrezzoUnitario() != null) {
			ordineRiga.setPrezzoUnitario(req.getPrezzoUnitario());
		}
		ordineRigaRepo.save(ordineRiga);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void rimuoviProdotto(OrdineRigaReq req) throws AcademyException {
		log.debug("Rimuovi prodotto dall'ordine: {}", req);
		OrdineRiga ordineRiga = ordineRigaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("riga-ordine-non-esistente")));
		ordineRigaRepo.delete(ordineRiga);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public List<OrdineRigaDTO> listByOrdine(Integer ordineId) {
		log.debug("Lista righe per ordine: {}", ordineId);
		List<OrdineRiga> righe = ordineRigaRepo.findAllByOrdineId(ordineId);
		List<OrdineRigaDTO> dtos = ordineRigaMapper.toDtoList(righe);
		for(OrdineRigaDTO dto: dtos)
			dto.setSubTotale(dto.getPrezzoUnitario().multiply(BigDecimal.valueOf(dto.getQuantita())));
		return dtos;
	}

}
