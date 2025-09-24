package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.PiattaformaReq;

/**
 * Interfaccia per il servizio di gestione delle piattaforme.
 * Fornisce metodi per creare, aggiornare, disattivare e recuperare le piattaforme.
 */
public interface PiattaformaService {

	/**
	 * Crea una nuova piattaforma.
	 *
	 * @param req 				Dati della piattaforma da aggiungere
	 * @return PiattaformaDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se i dati non sono validi o la piattaforma esiste già
	 */
	PiattaformaDTO crea(PiattaformaReq req) throws AcademyException;

	/**
	 * Aggiorna una piattaforma esistente.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se la piattaforma non esiste o i dati non sono validi
	 */
	void aggiorna(PiattaformaReq req) throws AcademyException;

	/**
	 * Disattiva una piattaforma (soft delete), marcandola come "non attiva".
	 * La piattaforma non viene rimossa fisicamente dal DB.
	 *
	 * @param id 				Richiesta contenente solo l'ID della piattaforma da rimuovere
	 * @throws AcademyException se la piattaforma non esiste
	 */
	void disattiva(PiattaformaReq req) throws AcademyException;

	/**
	 * Recupera una piattaforma in base al suo ID.
	 *
	 * @param id 				ID della piattaforma
	 * @return PiattaformaDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se la piattaforma non esiste
	 */
	PiattaformaDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutte le piattaforme attive.
	 *
	 * @return Lista di PiattaformaDTO
	 */
	List<PiattaformaDTO> listActive();
}