package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.MetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.MetodoPagamentoReq;

/**
 * Interfaccia per il servizio di gestione dei metodi di pagamento degli account.
 * Fornisce metodi per creare, aggiornare, disattivare e recuperare i metodi di pagamento.
 */
public interface MetodoPagamentoService {

	/**
	 * Aggiunge un nuovo metodo di pagamento per un account.
	 *
	 * @param req 				Dati del metodo di pagamento
	 * @return ID del metodo creato
	 * @throws AcademyException se i dati non sono validi
	 */
	Integer crea(MetodoPagamentoReq req) throws AcademyException;

	/**
	 * Aggiorna un metodo di pagamento esistente.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se il metodo non esiste o i dati non sono validi
	 */
	void aggiorna(MetodoPagamentoReq req) throws AcademyException;

	/**
	 * Disattiva un metodo di pagamento (soft delete), marcandolo come "non attiva".
	 * Il metodo di pagamento non viene rimosso fisicamente dal DB.
	 *
	 * @param req 				Richiesta contenente solo l'ID del metodo da disattivare
	 * @throws AcademyException se il metodo non esiste
	 */
	void disattiva(MetodoPagamentoReq req) throws AcademyException;

	/**
	 * Recupera un metodo di pagamento in base al suo ID.
	 *
	 * @param id 				ID del metodo di pagamento
	 * @return MetodoPagamentoDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se il metodo non esiste
	 */
	MetodoPagamentoDTO getById(Integer id) throws AcademyException;

	/**
	 * Restituisce tutti i metodi di pagamento associati ad un account.
	 *
	 * @param accountId 		ID dell'account
	 * @return lista dei metodi di pagamento
	 */
	List<MetodoPagamentoDTO> listByAccount(Integer accountId);
}