package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CategoriaReq;

/**
 * Interfaccia per il servizio di gestione delle categorie.
 * Fornisce metodi per creare, aggiornare, disattivare e recuperare le categorie.
 */
public interface CategoriaService {

	/**
	 * Aggiunge una nuova categoria.
	 *
	 * @param req 				Dati della categoria da creare
	 * @return CategoriaDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se i dati non sono validi o la categoria esiste già
	 */
	CategoriaDTO crea(CategoriaReq req) throws AcademyException;

	/**
	 * Aggiorna una categoria esistente.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se la categoria non esiste o i dati non sono validi
	 */
	void aggiorna(CategoriaReq req) throws AcademyException;

	/**
	 * Disattiva una categoria (soft delete), marcandola come "non attiva".
	 * La categoria non viene rimossa fisicamente dal DB.
	 *
	 * @param req 				Richiesta contenente solo l'ID della categoria da disattivare
	 * @throws AcademyException se la categoria non esiste
	 */
	void disattiva(CategoriaReq req) throws AcademyException;

	/**
	 * Recupera una categoria in base al suo ID.
	 *
	 * @param id 				ID della categoria
	 * @return CategoriaDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se la categoria non esiste
	 */
	CategoriaDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutte le categorie attive.
	 *
	 * @return lista di CategoriaDTO
	 */
	List<CategoriaDTO> listActive();
}