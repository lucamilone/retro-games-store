package com.betacom.retrogames.service.implementations;

import static com.betacom.retrogames.util.Utils.normalizza;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.PiattaformaMapper;
import com.betacom.retrogames.model.Piattaforma;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.PiattaformaService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PiattaformaImpl implements PiattaformaService {
	private final CacheManager cacheManager;
	private final MessaggioService msgS;
	private final PiattaformaRepository piattaformaRepo;
	private final PiattaformaMapper piattaformaMapper;

	public PiattaformaImpl(CacheManager cacheManager, MessaggioService msgS, PiattaformaRepository piattaformaRepo,
			PiattaformaMapper piattaformaMapper) {
		this.cacheManager = cacheManager;
		this.msgS = msgS;
		this.piattaformaRepo = piattaformaRepo;
		this.piattaformaMapper = piattaformaMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public PiattaformaDTO crea(PiattaformaReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico se esiste già una piattaforma con lo stesso codice
		Optional<Piattaforma> piattaformaOpt = piattaformaRepo.findByCodice(normalizza(req.getCodice()));
		if (piattaformaOpt.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("piattaforma-esistente"));
		}

		Piattaforma piattaforma = new Piattaforma();
		piattaforma.setCodice(normalizza(req.getCodice()));
		piattaforma.setNome(req.getNome());
		piattaforma.setAnnoUscitaPiattaforma(req.getAnnoUscitaPiattaforma());
		piattaforma.setAttivo(true);

		// Salvo la piattaforma
		Piattaforma saved = piattaformaRepo.save(piattaforma);

		// Creo il nuovo valore nella cache
		CachedPiattaforma piattaformaNew = new CachedPiattaforma(saved);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, piattaformaNew);

		log.debug("Piattaforma creata con successo. ID: {}", saved.getId());

		return piattaformaMapper.toDto(piattaforma);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(PiattaformaReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Controllo se la piattaforma è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("piattaforma-non-trovata"));
		}

		// Recupero dal DB per salvare i dati
		Piattaforma piattaforma = piattaformaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("piattaforma-non-trovata")));

		if (req.getCodice() != null) {
			piattaforma.setCodice(normalizza(req.getCodice()));
		}

		if (req.getNome() != null) {
			piattaforma.setNome(req.getNome());
		}

		if (req.getAnnoUscitaPiattaforma() != null) {
			piattaforma.setAnnoUscitaPiattaforma(req.getAnnoUscitaPiattaforma());
		}

		if (req.getAttivo() != null) {
			piattaforma.setAttivo(req.getAttivo());
		}

		// Salvo la piattaforma aggiornata
		Piattaforma saved = piattaformaRepo.save(piattaforma);

		// Aggiorno la cache
		CachedPiattaforma piattaformaUpd = new CachedPiattaforma(saved);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, piattaformaUpd);

		log.debug("Piattaforma aggiornata con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(PiattaformaReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Controllo se la piattaforma è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("piattaforma-non-trovata"));
		}

		// Recupero dal DB per salvare i dati
		Piattaforma piattaforma = piattaformaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("piattaforma-non-trovata")));

		// Disattivo la piattaforma
		piattaforma.setAttivo(false);

		// Salvo la piattaforma disattivata
		piattaformaRepo.save(piattaforma);

		// Rimuovo il record dalla cache
		cacheManager.removeRecordFromCachedTable(TabellaCostante.PIATTAFORMA, req.getId());

		log.debug("Piattaforma disattivata con successo. ID: {}", req.getId());
	}

	@Override
	public PiattaformaDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Controllo se la piattaforma è presente nella cache
		CachedPiattaforma cached = (CachedPiattaforma) cacheManager.getCachedEntryFromTable(TabellaCostante.PIATTAFORMA,
				id);

		if (cached != null) {
			// Se trovato in cache, ritorna DTO direttamente
			return piattaformaMapper.toDtoFromCached(cached);
		}

		// Se non presente, recupero dal DB
		Piattaforma piattaforma = piattaformaRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("piattaforma-non-trovata")));

		// Aggiorno la cache
		CachedPiattaforma piattaformaNew = new CachedPiattaforma(piattaforma);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, piattaformaNew);

		return piattaformaMapper.toDto(piattaforma);
	}

	@Override
	public List<PiattaformaDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo le piattaforme attive
		List<Piattaforma> piattaformeAttive = piattaformaRepo.findByAttivoTrue();

		return piattaformaMapper.toDtoList(piattaformeAttive);
	}
}
