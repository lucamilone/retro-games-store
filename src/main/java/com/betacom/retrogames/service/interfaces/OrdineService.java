package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.OrdineDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.OrdineReq;

/**
 * Interfaccia per il servizio di gestione degli ordini.
 * Fornisce metodi per creare (checkout), aggiornare e recuperare gli ordini.
 */
public interface OrdineService {

	/**
	 * Esegue il checkout del carrello creando un nuovo ordine.
	 * Imposta automaticamente lo stato iniziale e valida pagamento e indirizzo.
	 *
	 * @param req 				Dati necessari per creare l'ordine: righe, pagamento, indirizzo, accountId
	 * @return ID generato dell'ordine creato
	 * @throws AcademyException se i dati non sono validi o il checkout fallisce
	 */
	Integer crea(OrdineReq req) throws AcademyException;

	/**
	 * Aggiorna un ordine esistente.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se l'ordine non esiste
	 */
	void aggiorna(OrdineReq req) throws AcademyException;

	/**
	 * Aggiorna lo stato di un ordine (es. da IN_ATTESA a CONSEGNATO).
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se l'ordine non esiste o la transizione non è valida
	 */
	void aggiornaStato(OrdineReq req) throws AcademyException;

	/**
	 * Recupera un ordine in base al suo ID.
	 *
	 * @param id 				ID dell'ordine
	 * @return OrdineDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se l'ordine non esiste
	 */
	OrdineDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutti gli ordini di un account.
	 *
	 * @param accountId ID dell'account
	 * @return Lista di OrdineDTO
	 */
	List<OrdineDTO> listByAccount(Integer accountId);
}