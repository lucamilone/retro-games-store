package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.PagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.PagamentoReq;

/**
 * Interfaccia per il servizio di gestione dei pagamenti.
 * Fornisce metodi per creare, aggiornare e recuperare i pagamenti associati agli ordini.
 */
public interface PagamentoService {

	/**
	 * Crea un nuovo pagamento associato ad un ordine.
	 * Tipicamente chiamato durante il checkout.
	 *
	 * @param req  				Richiesta contenente ordineId, metodo di pagamento e totale
	 * @return ID generato del pagamento
	 * @throws AcademyException se i dati non sono validi o il pagamento non può essere creato
	 */
	Integer crea(PagamentoReq req) throws AcademyException;

	/**
	 * Aggiorna lo stato di un pagamento o altri dati modificabili.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se il pagamento non esiste o i dati non sono validi
	 */
	void aggiorna(PagamentoReq req) throws AcademyException;

	/**
	 * Recupera un pagamento in base al suo ID.
	 *
	 * @param id 				ID del pagamento
	 * @return PagamentoDTO contenente tutti i dati rilevanti
	 * @throws AcademyException se il pagamento non esiste
	 */
	PagamentoDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutti i pagamenti effettuati.
	 * Utile per report amministrativi o cronologia ordini.
	 *
	 * @return lista di PagamentoDTO
	 */
	List<PagamentoDTO> list();
}