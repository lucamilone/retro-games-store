package com.betacom.retrogames.service.implementations;

import static com.betacom.retrogames.util.Utils.normalizza;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.CredenzialeMapper;
import com.betacom.retrogames.model.Credenziale;
import com.betacom.retrogames.repository.CredenzialeRepository;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.service.interfaces.CredenzialeService;
import com.betacom.retrogames.service.interfaces.MessaggioService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CredenzialeImpl implements CredenzialeService {
	private final CredenzialeRepository credenzialeRepo;
	private final MessaggioService msgS;
	private final CredenzialeMapper credenzialeMapper;

	public CredenzialeImpl(CredenzialeRepository credenzialeRepo, MessaggioService msgS,
			CredenzialeMapper credenzialeMapper) {
		this.credenzialeRepo = credenzialeRepo;
		this.msgS = msgS;
		this.credenzialeMapper = credenzialeMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer crea(CredenzialeReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico se esiste già una credenziale con la stessa email
		Optional<Credenziale> credenzialeOpt = credenzialeRepo.findByEmail(req.getEmail());
		if (credenzialeOpt.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("email-esistente"));
		}

		Credenziale credenziale = new Credenziale();
		credenziale.setEmail(normalizza(req.getEmail()));
		credenziale.setPassword(req.getPassword());
		credenziale.setUltimoLogin(null);
		credenziale.setAttivo(true);

		// Salvo la credenziale
		credenziale = credenzialeRepo.save(credenziale);

		log.debug("Credenziale creata con successo. ID: {}", credenziale.getId());

		// Restituisco l'id generato
		return credenziale.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiornaEmail(Integer credenzialeId, String nuovaEmail) throws AcademyException {
		log.debug("Aggiorna Email - Credenziale ID: {} - Nuova Email: {}", credenzialeId, nuovaEmail);

		// Verifico se esiste già una credenziale con la stessa email
		Optional<Credenziale> credenzialeOpt = credenzialeRepo.findByEmail(nuovaEmail);
		if (credenzialeOpt.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("email-esistente"));
		}

		// Verifico l'esistenza della credenziale
		Credenziale credenziale = credenzialeRepo.findById(credenzialeId)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("credenziale-non-trovata")));

		// Aggiorno l'email
		credenziale.setEmail(normalizza(nuovaEmail));

		// Salvo la credenziale aggiornata
		credenzialeRepo.save(credenziale);

		log.debug("Email aggiornata con successo. Credenziale ID: {}", credenzialeId);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiornaPassword(Integer credenzialeId, String nuovaPassword) throws AcademyException {
		log.debug("Aggiorna Password - Credenziale ID: {} - Nuova Password: {}", credenzialeId, nuovaPassword);

		// Verifico l'esistenza della credenziale
		Credenziale credenziale = credenzialeRepo.findById(credenzialeId)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("credenziale-non-trovata")));

		// Aggiorno la password
		credenziale.setPassword(nuovaPassword);

		// Salvo la credenziale aggiornata
		credenzialeRepo.save(credenziale);

		log.debug("Password aggiornata con successo. Credenziale ID: {}", credenzialeId);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(CredenzialeReq req) throws AcademyException {
		log.debug("Disattiva: {}", req.getId());

		// Verifico l'esistenza della credenziale
		Credenziale credenziale = credenzialeRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("credenziale-non-trovata")));

		// Disattivo la credenziale
		credenziale.setAttivo(false);

		// Salvo la credenziale disattivata
		credenzialeRepo.save(credenziale);

		log.debug("Credenziale disattivata con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void riattiva(CredenzialeReq req) throws AcademyException {
		log.debug("Riattiva: {}", req.getId());

		// Verifico l'esistenza della credenziale
		Credenziale credenziale = credenzialeRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("credenziale-non-trovata")));

		// Riattivo la credenziale
		credenziale.setAttivo(true);

		// Salvo la credenziale riattivata
		credenzialeRepo.save(credenziale);

		log.debug("Credenziale riattivata con successo. ID: {}", req.getId());
	}

	@Override
	public CredenzialeDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Recupero la credenziale dal DB
		Credenziale credenziale = credenzialeRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("credenziale-non-trovata")));

		return credenzialeMapper.toDto(credenziale);
	}

	@Override
	public List<CredenzialeDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo le credenziali attive
		List<Credenziale> credenzialiAttive = credenzialeRepo.findByAttivoTrue();

		return credenzialeMapper.toDtoList(credenzialiAttive);
	}
}
