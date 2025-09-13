package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.AccountReq;

/**
 * Interfaccia per il servizio di gestione degli account.
 * Fornisce metodi per creare, aggiornare, disattivare e recuperare account.
 */
public interface AccountService {

	/**
	* Crea un nuovo account con dati anagrafici, indirizzo e credenziali.
	* Esegue controlli di validità come email/username univoci e formato password.
	*
	* @param req 			   Dati dell'account da registrare
	* @return ID generato dell'account creato
	* @throws AcademyException se i dati non sono validi o l'account esiste già
	*/
	Integer crea(AccountReq req) throws AcademyException;

	/**
	 * Aggiorna i dati personali dell'account (nome, cognome, indirizzo).
	 * Non modifica credenziali e ruolo.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se l'account non esiste o i dati non sono validi
	 */
	void aggiorna(AccountReq req) throws AcademyException;

	/**
	* Disattiva un account (soft delete), marcandolo come "non attivo".
	* L'account non viene rimosso fisicamente dal DB.
	*
	* @param req 			   Richiesta contenente solo l'ID dell'account da disattivare
	* @throws AcademyException se l'account non esiste
	*/
	void disattiva(AccountReq req) throws AcademyException;

	/**
	 * Riattiva un account, marcandolo come "attivo".
	 *
	 * @param req 				Richiesta contenente solo l'ID dell'account da riattivare
	 * @throws AcademyException se l'account non esiste
	 */
	void riattiva(AccountReq req) throws AcademyException;

	/**
	 * Recupera un account in base al suo ID.
	 *
	 * @param id 				ID dell'account
	 * @return AccountDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se l'account non esiste
	 */
	AccountDTO getById(Integer id) throws AcademyException;

	/**
	* Recupera tutti gli account attivi.
	*
	* @return lista di AccountDTO
	*/
	List<AccountDTO> listActive();
}
