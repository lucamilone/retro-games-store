package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.OrdineRigaReq;

/**
 * Interfaccia per il servizio di gestione delle righe di un ordine.
 * Fornisce metodi per aggiungere, aggiornare, rimuovere ed elencare i prodotti in un ordine.
 */
public interface OrdineRigaService {

	/**
	 * Aggiunge un prodotto a un ordine esistente creando una nuova riga.
	 *
	 * @param req 				Dati della riga da aggiungere (ordineId, prodottoId, quantità, prezzo)
	 * @return ID della riga appena creata
	 * @throws AcademyException se l'ordine non esiste o i dati non sono validi
	 */
	Integer aggiungiProdotto(OrdineRigaReq req) throws AcademyException;

	/**
	 * Aggiorna quantità e prezzo di un prodotto già presente nell'ordine.
	 *
	 * @param req 				Dati della riga da aggiornare (ID riga, nuova quantità, nuovo prezzoUnitario)
	 * @throws AcademyException se la riga non esiste o i dati sono invalidi
	 */
	void aggiornaRiga(OrdineRigaReq req) throws AcademyException;

	/**
	 * Rimuove un prodotto dall'ordine.
	 *
	 * @param req 				Dati della riga da rimuovere (ID riga)
	 * @throws AcademyException se la riga non esiste
	 */
	void rimuoviProdotto(OrdineRigaReq req) throws AcademyException;

	/**
	 * Recupera tutte le righe associate a un ordine specifico.
	 *
	 * @param ordineId ID dell'ordine
	 * @return Lista di OrdineRigaDTO
	 */
	List<OrdineRigaDTO> listByOrdine(Integer ordineId);
}