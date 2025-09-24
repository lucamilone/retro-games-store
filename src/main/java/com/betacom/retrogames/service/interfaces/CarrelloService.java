package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.CarrelloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CarrelloReq;

/**
 * Interfaccia per il servizio di gestione dei carrelli.
 * Fornisce metodi per creare, svuotare e recuperare un carrello.
 */
public interface CarrelloService {

	/**
	* Crea un nuovo carrello vuoto associato a un account esistente.
	* Questo metodo viene tipicamente chiamato dal servizio di registrazione dell'account.
	*
	* @param req   	   AccountId a cui associare il nuovo carrello
	* @return ID del carrello appena creato
	* @throws AcademyException se l'account non esiste
	*/
	Integer creaPerAccount(CarrelloReq req) throws AcademyException;

	/**
	 * Svuota tutte le righe di un carrello, effettuando un reset completo.
	 *
	 * @param req 				ID del carrello da svuotare
	 * @throws AcademyException se si verifica un errore durante l'operazione
	 */
	void svuotaCarrello(CarrelloReq req) throws AcademyException;

	/**
	* Recupera il carrello associato a un account specifico.
	*
	* @param accountId 		   ID dell'account
	* @return CarrelloDTO contenente tutti i dati rilevanti
	* @throws AcademyException se il carrello non esiste o si verifica un errore nel recupero
	*/
	CarrelloDTO getCarrelloByAccount(Integer accountId) throws AcademyException;
}