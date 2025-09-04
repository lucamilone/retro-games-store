package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.CarrelloRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CarrelloRigaReq;

/**
 * Interfaccia per il servizio di gestione delle righe del carrello.
 * Fornisce metodi per aggiungere, aggiornare, rimuovere ed elencare i prodotti nel carrello.
 */
public interface CarrelloRigaService {

	/**
	 * Aggiunge un prodotto al carrello creando una nuova riga.
	 *
	 * @param req 				Dati del prodotto da aggiungere
	 * @return ID della riga appena creata
	 * @throws AcademyException se si verifica un errore durante l'aggiunta del prodotto
	 */
	Integer aggiungiProdotto(CarrelloRigaReq req) throws AcademyException;

	/**
	 * Aggiorna il prodotto e la quantità di un prodotto già presente nel carrello.
	 *
	 * @param req 				ID del carrello, ID della riga e la nuova quantità
	 * @throws AcademyException se la riga non esiste o si verifica un errore durante l'aggiornamento
	 */
	void aggiornaRiga(CarrelloRigaReq req) throws AcademyException;

	/**
	* Rimuove un prodotto dal carrello.
	*
	* @param req 		   	   ID del carrello e ID della riga da rimuovere
	* @throws AcademyException se la riga non esiste o si verifica un errore durante la rimozione
	*/
	void rimuoviProdotto(CarrelloRigaReq req) throws AcademyException;

	/**
	 * Restituisce tutte le righe di un determinato carrello.
	 *
	 * @param carrelloId 	   ID del carrello
	 * @return lista di CarrelloRigaDTO
	 */
	List<CarrelloRigaDTO> listByCarrello(Integer carrelloId);
}