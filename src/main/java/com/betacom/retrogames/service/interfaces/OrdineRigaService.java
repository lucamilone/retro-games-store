package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.OrdineRigaDTO;

/**
 * Interfaccia per il servizio di gestione delle righe di un ordine.
 * Fornisce metodi per aggiungere, aggiornare, rimuovere ed elencare i prodotti in un ordine.
 */
public interface OrdineRigaService {

	/**
	 * Recupera tutte le righe associate a un ordine specifico.
	 *
	 * @param ordineId ID dell'ordine
	 * @return Lista di OrdineRigaDTO
	 */
	List<OrdineRigaDTO> listByOrdine(Integer ordineId);
}