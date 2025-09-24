package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;

/**
 * Interfaccia per il servizio di gestione dei ruoli.
 * Permette di creare, aggiornare, disattivare e recuperare i ruoli degli account.
 */
public interface RuoloService {

	/**
	 * Crea un nuovo ruolo.
	 *
	 * @param req 				Dati del ruolo da creare
	 * @return RuoloDTO contenente i dati rilevanti
	 * @throws AcademyException se i dati non sono validi o il ruolo esiste già
	 */
	RuoloDTO crea(RuoloReq req) throws AcademyException;

	/**
	 * Aggiorna un ruolo esistente.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se il ruolo non esiste o i dati non sono validi
	 */
	void aggiorna(RuoloReq req) throws AcademyException;

	/**
	 * Disattiva un ruolo (soft delete), marcandolo come "non attivo".
	 * Il ruolo non viene rimosso fisicamente dal DB.
	 *
	 * @param req 				Richiesta contenente solo l'ID del ruolo da disattivare
	 * @throws AcademyException se il ruolo non esiste
	 */
	void disattiva(RuoloReq req) throws AcademyException;

	/**
	 * Recupera un ruolo in base al suo ID.
	 *
	 * @param id 				ID del ruolo
	 * @return RuoloDTO contenente i dati rilevanti
	 * @throws AcademyException se il ruolo non esiste
	 */
	RuoloDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutti i ruoli attivi.
	 *
	 * @return Lista di RuoloDTO
	 */
	List<RuoloDTO> listActive();
}