package com.betacom.retrogames.service.implementations;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.betacom.retrogames.dto.OrdineDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.OrdineMapper;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.Indirizzo;
import com.betacom.retrogames.model.Ordine;
import com.betacom.retrogames.model.enums.StatoOrdine;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.OrdineRepository;
import com.betacom.retrogames.repository.PagamentoRepository;
import com.betacom.retrogames.request.OrdineReq;
import com.betacom.retrogames.request.OrdineRigaReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.OrdineRigaService;
import com.betacom.retrogames.service.interfaces.OrdineService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
@Service
public class OrdineImpl implements OrdineService {
	private final OrdineRepository ordineRepo;
	private final OrdineMapper ordineMapper;
	private final OrdineRigaService ordineRigaService;
	private final AccountRepository accountRepo;
	private final PagamentoRepository pagamentoRepo;
	private final MessaggioService msgS;

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer crea(OrdineReq req) throws AcademyException {
		log.debug("Crea ordine: {}", req);

		Account account = accountRepo.findById(req.getAccountId())
			.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-esistente")));
		
		Indirizzo indirizzo;
		if(req.getIndirizzoSpedizione() != null) {
			indirizzo = new Indirizzo();
			indirizzo.setVia(req.getIndirizzoSpedizione().getVia());
			indirizzo.setCitta(req.getIndirizzoSpedizione().getCitta());
			indirizzo.setCap(req.getIndirizzoSpedizione().getCap());
			indirizzo.setPaese(req.getIndirizzoSpedizione().getPaese());
		} else {
			indirizzo = account.getIndirizzo();
		}
		
		Ordine ordine = new Ordine();
		ordine.setAccount(account);
		ordine.setIndirizzoSpedizione(indirizzo);
		if(req.getPagamento() != null)
			ordine.setPagamento(pagamentoRepo.findById(req.getPagamento().getId())
				.orElseThrow(() -> new AcademyException("Pagamento non esistente")));
		ordine.setTotale(BigDecimal.ZERO);
		Integer returnId = ordineRepo.save(ordine).getId();
		
		for(OrdineRigaReq rigaReq : req.getRighe()) {
			rigaReq.setOrdineId(returnId);
			ordineRigaService.aggiungiProdotto(rigaReq);
		}
		ordine.setTotale(ordine.getTotale());
		ordineRepo.save(ordine);
		return returnId;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(OrdineReq req) throws AcademyException {
	    log.debug("Aggiorna ordine: {}", req);

	    Ordine ordine = ordineRepo.findById(req.getId())
	        .orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-esistente")));

	    if(req.getAccountId() != null) {
	    	Account account = accountRepo.findById(req.getAccountId())
		            .orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-esistente")));
		    ordine.setAccount(account);
	    }
	    
	    Indirizzo indirizzo = ordine.getIndirizzoSpedizione();
	    if(req.getIndirizzoSpedizione() != null) {
	    	if(req.getIndirizzoSpedizione().getVia() != null) {
		    	indirizzo.setVia(req.getIndirizzoSpedizione().getVia());
		    }
		    if(req.getIndirizzoSpedizione().getCitta() != null) {
		    	indirizzo.setCitta(req.getIndirizzoSpedizione().getCitta());
		    }
		    if(req.getIndirizzoSpedizione().getCap() != null) {
		    	indirizzo.setCap(req.getIndirizzoSpedizione().getCap());
		    }
		    if(req.getIndirizzoSpedizione().getPaese() != null) {
		    	indirizzo.setPaese(req.getIndirizzoSpedizione().getPaese());
		    }
		    ordine.setIndirizzoSpedizione(indirizzo);
	    }
	    
	    if(req.getPagamento() != null) {
	        ordine.setPagamento(
	            pagamentoRepo.findById(req.getPagamento().getId())
	                .orElseThrow(() -> new AcademyException(msgS.getMessaggio("pagamento-non-esistente")))
	        );
	    }

        ordine.setTotale(ordine.getTotale());
	    ordineRepo.save(ordine);
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	public void aggiornaStato(Integer ordineId, String nuovoStato) throws AcademyException {
	    log.debug("Aggiorna stato ordine: id={}, nuovoStato={}", ordineId, nuovoStato);

	    Ordine ordine = ordineRepo.findById(ordineId)
	        .orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-esistente")));

	    // Se hai un enum per lo stato, usa quello; altrimenti salva la stringa normalizzata.
	    try {
	        ordine.setStatoOrdine(StatoOrdine.valueOf(nuovoStato.toUpperCase()));
	    } catch (Exception ex) {
	        throw new AcademyException(msgS.getMessaggio("stato-ordine-non-valido"));
	    }
	    ordineRepo.save(ordine);
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	public OrdineDTO getById(Integer ordineId) throws AcademyException {
	    log.debug("Recupera ordine per id: {}", ordineId);
	    Ordine ordine = ordineRepo.findById(ordineId)
	        .orElseThrow(() -> new AcademyException(msgS.getMessaggio("ordine-non-esistente")));
	    OrdineDTO dto = ordineMapper.toDto(ordine);
	    dto.setTotaleQuantita(ordine.getTotaleQuantita());
	    return dto;
	}


	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	public List<OrdineDTO> listByAccount(Integer accountId) {
	    log.debug("Recupera ordini per account: {}", accountId);
	    List<Ordine> ordini = ordineRepo.findAllByAccountId(accountId);
	    List<OrdineDTO> dtos = ordineMapper.toDtoList(ordini);
	    for(int i=0; i<ordini.size(); i++)
	    	dtos.get(i).setTotaleQuantita(ordini.get(i).getTotaleQuantita());
	    return dtos;
	}

}
