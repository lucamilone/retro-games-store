package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;

/**
 * Interfaccia per il servizio di gestione dei tipi di metodo di pagamento.
 * Fornisce metodi per creare, aggiornare, disattivare e recuperare i tipi di metodo di pagamento.
 */
public interface TipoMetodoPagamentoService {

	/**
	 * Crea un nuovo tipo di metodo di pagamento.
	 *
	 * @param req 				Dati del tipo di metodo di pagamento da creare
	 * @return ID del tipo di metodo di pagamento creato
	 * @throws AcademyException se i dati non sono validi o il tipo esiste già
	 */
	Integer crea(TipoMetodoPagamentoReq req) throws AcademyException;

	/**
	 * Aggiorna un tipo di metodo di pagamento esistente.
	 *
	 * @param req 				Dati da aggiornare
	 * @throws AcademyException se il tipo non esiste o i dati non sono validi
	 */
	void aggiorna(TipoMetodoPagamentoReq req) throws AcademyException;

	/**
	 * Disattiva un tipo di metodo di pagamento (soft delete), marcandolo come "non attivo".
	 * Il tipo di metodo di pagamento non viene rimosso fisicamente dal DB.
	 *
	 * @param req 				Richiesta contenente solo l'ID del tipo da disattivare
	 * @throws AcademyException se il tipo non esiste
	 */
	void disattiva(TipoMetodoPagamentoReq req) throws AcademyException;

	/**
	 * Recupera un tipo di metodo di pagamento in base al suo ID.
	 *
	 * @param id 				ID del tipo di metodo di pagamento
	 * @return TipoMetodoPagamentoDTO contenente i dati rilevanti
	 * @throws AcademyException se il tipo non esiste
	 */
	TipoMetodoPagamentoDTO getById(Integer id) throws AcademyException;

	/**
	 * Recupera tutti i tipi di metodo di pagamento attivi.
	 *
	 * @return Lista di TipoMetodoPagamentoDTO
	 */
	List<TipoMetodoPagamentoDTO> listActive();
}
