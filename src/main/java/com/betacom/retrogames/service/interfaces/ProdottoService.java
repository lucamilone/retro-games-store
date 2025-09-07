package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.ProdottoReq;

/**
 * Interfaccia per il servizio di gestione dei prodotti.
 * Fornisce metodi per creare, aggiornare, disattivare e recuperare i prodotti.
 */
public interface ProdottoService {

	/**
	 * Crea un nuovo prodotto nel catalogo.
	 *
	 * @param req 				Dati del prodotto da creare
	 * @return ID generato del prodotto creato
	 * @throws AcademyException se dati non validi o SKU già esistente
	 */
	Integer crea(ProdottoReq req) throws AcademyException;

	/**
	 * Aggiorna le informazioni di un prodotto esistente.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se prodotto non trovato o dati non validi
	 */
	void aggiorna(ProdottoReq req) throws AcademyException;

	/**
	 * Disattiva un prodotto (soft delete), marcandolo come "non attivo".
	 * Il prodotto non viene rimosso fisicamente dal DB.
	 *
	 * @param prodottoId 		Richiesta contenente solo l'ID del prodotto da disattivare
	 * @throws AcademyException se prodotto non trovato
	 */
	void disattiva(ProdottoReq req) throws AcademyException;

	/**
	 * Recupera un prodotto in base al suo ID.
	 *
	 * @param id 				ID del prodotto
	 * @return ProdottoDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se il prodotto non esiste
	 */
	ProdottoDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutti i prodotti attivi nel catalogo.
	 *
	 * @return lista di ProdottoDTO
	 */
	List<ProdottoDTO> listActive();
}