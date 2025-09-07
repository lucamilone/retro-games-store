package com.betacom.retrogames.service.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedRuolo;
import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.RuoloMapper;
import com.betacom.retrogames.model.Ruolo;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.RuoloService;
import com.betacom.retrogames.util.Utils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RuoloImpl extends Utils implements RuoloService {
	private final CacheManager cacheManager;
	private final MessaggioService msgS;
	private final RuoloRepository ruoloRepo;
	private final RuoloMapper ruoloMapper;

	public RuoloImpl(CacheManager cacheManager, MessaggioService msgS, RuoloRepository ruoloRepo,
			RuoloMapper ruoloMapper) {
		this.cacheManager = cacheManager;
		this.msgS = msgS;
		this.ruoloRepo = ruoloRepo;
		this.ruoloMapper = ruoloMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer crea(RuoloReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico se esiste già un ruolo con lo stesso nome
		Optional<Ruolo> ruoloOpt = ruoloRepo.findByNome(req.getNome());

		if (ruoloOpt.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("ruolo-esiste"));
		}

		Ruolo ruolo = new Ruolo();
		ruolo.setNome(req.getNome());
		ruolo.setAttivo(true);

		// Salvo il ruolo
		Ruolo saved = ruoloRepo.save(ruolo);

		// Creo il nuovo valore nella cache
		CachedRuolo newRuolo = new CachedRuolo(saved);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.RUOLO, newRuolo);

		// restituisce l'id generato
		return saved.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(RuoloReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Controllo rapido in cache
		if (!cacheManager.isRecordCached(TabellaCostante.RUOLO, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("ruolo-non-esiste"));
		}

		// Recupero dal DB per salvare i dati
		Ruolo ruolo = ruoloRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ruolo-non-esiste")));

		ruolo.setNome(req.getNome());

		// Salvo il ruolo aggiornato
		ruoloRepo.save(ruolo);

		// Aggiorno la cache
		CachedRuolo newRuolo = new CachedRuolo(ruolo);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.RUOLO, newRuolo);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(RuoloReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Controllo rapido in cache
		if (!cacheManager.isRecordCached(TabellaCostante.RUOLO, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("ruolo-non-esiste"));
		}

		// Recupero dal DB per salvare i dati
		Ruolo ruolo = ruoloRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ruolo-non-esiste")));

		// Disattivo il ruolo
		ruolo.setAttivo(false);

		// Salvo il ruolo disattivato
		ruoloRepo.save(ruolo);

		// Rimuovo il record dalla cache
		cacheManager.removeRecordFromCachedTable(TabellaCostante.RUOLO, req.getId());
	}

	@Override
	public RuoloDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Controllo in cache
		CachedRuolo cached = (CachedRuolo) cacheManager.getCachedEntryFromTable(TabellaCostante.RUOLO, id);

		if (cached != null) {
			// Se trovato in cache, ritorna DTO direttamente
			return ruoloMapper.toDtoFromCached(cached);
		}

		// Se non presente, recupero dal DB
		Ruolo ruolo = ruoloRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("ruolo-non-esiste")));

		// Aggiorno la cache
		CachedRuolo newRuolo = new CachedRuolo(ruolo);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.RUOLO, newRuolo);

		// Converto il ruolo trovato in un DTO e lo restituisco
		return ruoloMapper.toDto(ruolo);
	}

	@Override
	public List<RuoloDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo i ruoli attivi
		List<Ruolo> ruoliAttivi = ruoloRepo.findByAttivoTrue();

		return ruoloMapper.toDtoList(ruoliAttivi);
	}
}
