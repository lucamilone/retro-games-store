package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CredenzialeReq;

/**
 * Interfaccia per il servizio di gestione delle credenziali degli account.
 * Fornisce metodi per creare, aggiornare email o password
 * e per operazioni amministrative come disattivazione e visualizzazione.
 */
public interface CredenzialeService {

	/**
	 * Crea le credenziali di un nuovo account.
	 * Viene chiamato tipicamente durante la registrazione dell'account.
	 *
	 * @param req				Dati della credenziale (email + password)
	 * @return CredenzialeDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se email già esistente o dati non validi
	 */
	CredenzialeDTO crea(CredenzialeReq req) throws AcademyException;

	/**
	 * Aggiorna solo l'email di una credenziale esistente.
	 *
	 * @param req				Dati della credenziale (ID + Nuova email)
	 * @throws AcademyException se credenziale non trovata o email già in uso
	 */
	void aggiornaEmail(CredenzialeReq req) throws AcademyException;

	/**
	 * Aggiorna solo la password di una credenziale esistente.
	 *
	 * @param req				Dati della credenziale (ID + Nuova password)
	 * @throws AcademyException se credenziale non trovata o dati non validi
	 */
	void aggiornaPassword(CredenzialeReq req) throws AcademyException;

	/**
	 * Disattiva una credenziale (soft delete), marcandola come "non attiva".
	 * La credenziale non viene rimossa fisicamente dal DB.
	 *
	 * @param req 				Richiesta contenente solo l'ID della credenziale da disattivare
	 * @throws AcademyException se credenziale non esiste
	 */
	void disattiva(CredenzialeReq req) throws AcademyException;

	/**
	 * Riattiva una credenziale, marcandola come "attiva".
	 *
	 * @param req 				Richiesta contenente solo l'ID della credenziale da riattivare
	 * @throws AcademyException se credenziale non esiste
	 */
	void riattiva(CredenzialeReq req) throws AcademyException;

	/**
	 * Esegue l'autenticazione di un utente in base alle credenziali fornite.
	 *
	 * @param req 				Dati della credenziale (email + password)
	 * @return CredenzialeDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se credenziale sono errate
	 */
	CredenzialeDTO signIn(CredenzialeReq req) throws AcademyException;

	/**
	 * Recupera una credenziale in base al suo ID.
	 *
	 * @param id 				ID della credenziale
	 * @return CredenzialeDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se credenziale non esiste
	 */
	CredenzialeDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutte le credenziali attive (solo per amministratori).
	 *
	 * @return lista di CredenzialeDTO
	 */
	List<CredenzialeDTO> listActive();
}