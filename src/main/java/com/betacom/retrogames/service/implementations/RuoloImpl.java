package com.betacom.retrogames.service.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.RuoloMapper;
import com.betacom.retrogames.model.Ruolo;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.service.interfaces.RuoloService;
import com.betacom.retrogames.util.Utils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class RuoloImpl extends Utils implements RuoloService {
	private final CacheManager cacheManager;
	private final RuoloRepository ruoloRepo;
	private final RuoloMapper ruoloMapper;

	public RuoloImpl(CacheManager cacheManager, RuoloRepository ruoloRepo, RuoloMapper ruoloMapper) {
		super();
		this.cacheManager = cacheManager;
		this.ruoloRepo = ruoloRepo;
		this.ruoloMapper = ruoloMapper;
	}

	@Override
	public Integer crea(RuoloReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico se esiste già un ruolo con lo stesso nome
		Optional<Ruolo> ruoloOpt = ruoloRepo.findByNome(req.getNome());

		if (ruoloOpt.isPresent()) {
			throw new AcademyException("Ruolo con nome " + req.getNome() + " già esistente.");
		}

		Ruolo ruolo = new Ruolo();
		ruolo.setNome(req.getNome());
		ruolo.setAttivo(true);

		// Salvo il ruolo e restituisce l'id generato
		return ruoloRepo.save(ruolo).getId();
	}

	@Override
	public void aggiorna(RuoloReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Verifico se il ruolo esiste
		Optional<Ruolo> ruoloOpt = ruoloRepo.findById(req.getId());

		if (ruoloOpt.isEmpty()) {
			throw new AcademyException("Ruolo con id " + req.getId() + " non trovato.");
		}

		Ruolo ruolo = ruoloOpt.get();

		// Se il nome non è nullo, aggiorno il nome del ruolo
		ruolo.setNome(req.getNome() != null ? req.getNome() : ruolo.getNome());

		// Salvo il ruolo aggiornato
		ruoloRepo.save(ruolo);
	}

	@Override
	public void disattiva(RuoloReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Verifico se il ruolo esiste
		Optional<Ruolo> ruoloOpt = ruoloRepo.findById(req.getId());

		if (ruoloOpt.isEmpty()) {
			throw new AcademyException("Ruolo con id " + req.getId() + " non trovato.");
		}

		Ruolo ruolo = ruoloOpt.get();

		// Imposto il ruolo come non attivo
		ruolo.setAttivo(false);

		// Salvo il ruolo disattivato
		ruoloRepo.save(ruolo);
	}

	@Override
	public RuoloDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Verifico se il ruolo esiste
		Optional<Ruolo> ruoloOpt = ruoloRepo.findById(id);

		if (ruoloOpt.isEmpty()) {
			throw new AcademyException("Ruolo con id " + id + " non trovato.");
		}

		Ruolo ruolo = ruoloOpt.get();

		// Converto il ruolo trovato in un DTO e lo restituisco
		return ruoloMapper.toDto(ruolo);
	}

	@Override
	public List<RuoloDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo i ruoli attivi e li converte in DTO
		List<Ruolo> ruoliAttivi = ruoloRepo.findByAttivoTrue();

		return ruoloMapper.toDtos(ruoliAttivi);
	}
}
