package com.betacom.retrogames.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.enums.TabellaCostante;

@Service
public class CacheManager {

	/**
	 * Cache centralizzata per tutte le costanti dell'applicazione,
	 * utilizzata per validazioni e lookup rapidi senza accesso al database.
	 * 
	 * Struttura della cache:
	 *   - Chiave esterna: Identifica la tabella logica cacheata ({@link TabellaCostante})
	 *   - Valore esterno: Mappa interna immutabile dei record della tabella che associa:
	 *     - Chiave interna: ID che identifica il record
	 *     - Valore interno: Istanza di {@link CacheableEntry} (Es. {@link CachedCategoria})
	 *
	 * Note implementative:
	 *   - Viene usato {@link ConcurrentHashMap} per garantire la thread safety
	 *     della cache principale.
	 *   - Le mappe interne sono rese immutabili per evitare modifiche accidentali
	 *     e garantire consistenza tra thread.
	 * 
	 * Convenzioni nei commenti dei metodi:
	 * 	 - "Tabella" = la mappa esterna (TabellaCostante → mappa interna)
	 *   - "Mappa interna" = Map<Integer, ? extends CacheableEntry>
	 *   - "Record / Oggetto" = istanza concreta che implementa CacheableEntry
	 */
	private final Map<TabellaCostante, Map<Integer, ? extends CacheableEntry>> cache = new ConcurrentHashMap<>();

	/*
	 * ===== METODI PER LA CACHE DELLE COSTANTI =====
	 */

	/**
	 * Verifica se l'ID è presente nella mappa interna della tabella specificata.
	 *
	 * <p>Se la tabella non è presente nella cache,
	 * viene lanciata un'eccezione {@link IllegalArgumentException} tramite
	 * {@link #getCachedRecords(TabellaCostante)}.</p>
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @param id  						ID del record da verificare
	 * @return {@code true} se il record esiste nella cache, {@code false} altrimenti
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	public boolean isRecordCached(TabellaCostante nomeTabella, Integer id) {
		return getCachedRecords(nomeTabella).containsKey(id);
	}

	/**
	 * Restituisce l'oggetto cacheable corrispondente all'ID nella tabella specificata.
	 *
	 * <p>Se la tabella non è presente nella cache,
	 * viene lanciata un'eccezione {@link IllegalArgumentException} tramite
	 * {@link #getCachedRecords(TabellaCostante)}.</p>
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @param id  						ID dell'oggetto cacheable da recuperare
	 * @return l'oggetto cacheable corrispondente all'ID, o {@code null} se non presente
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	public CacheableEntry getCachedEntryFromTable(TabellaCostante nomeTabella, Integer id) {
		return getCachedRecords(nomeTabella).get(id);
	}

	/**
	 * Restituisce il valore leggibile di un record da esporre al frontend:
	 * - Di default il nome
	 * - Se la tabella è PIATTAFORMA, restituisce il codice
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @param id          				ID del record
	 * @return stringa da esporre al frontend, o null se il record non esiste
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	public String getCachedRecordDisplay(TabellaCostante nomeTabella, Integer id) {
		CacheableEntry entry = getCachedRecords(nomeTabella).get(id);

		if (entry == null) {
			return null;
		}

		switch (nomeTabella) {
		case PIATTAFORMA:
			// Restituisce il codice per la piattaforma
			return ((CachedPiattaforma) entry).getCodice();
		default:
			// Restituisce il nome per tutte le altre tabelle
			return entry.getNome();
		}
	}

	/**
	 * Inserisce o aggiorna un record singolo nella mappa interna della tabella specificata.
	 *
	 * <p>Funzionamento:
	 *   - Se la tabella non esiste in cache, viene creata una nuova mappa interna.
	 *   - Se la tabella esiste, viene copiata la mappa esistente e
	 *     aggiunto/aggiornato l'elemento.
	 *   - La mappa interna viene sempre resa immutabile per garantire consistenza
	 *     e thread safety.</p>
	 *
	 * @param <T>          Tipo del record da inserire (deve implementare {@link CacheableEntry})
	 * @param nomeTabella  Tabella logica di riferimento nella cache
	 * @param record       Record cacheabile da inserire o aggiornare
	 * @return 			   Mappa immutabile (chiave → record cacheabile)
	 */
	public <T extends CacheableEntry> void addOrUpdateRecordInCachedTable(TabellaCostante nomeTabella, T record) {
		cache.compute(nomeTabella, (k, mappaVecchia) -> {
			Map<Integer, CacheableEntry> nuovaMappa = new HashMap<>();

			if (mappaVecchia != null) {
				// Copia gli elementi esistenti
				nuovaMappa.putAll(mappaVecchia);
			}

			// Inserisce o aggiorna l’elemento
			nuovaMappa.put(record.getId(), record);

			// Rende la mappa immutabile per thread-safety
			return Collections.unmodifiableMap(nuovaMappa);
		});
	}

	/**
	 * Rimuove un record singolo dalla mappa interna della tabella specificata.
	 *
	 * <p>La cache è strutturata come:
	 * <pre>
	 *   TabellaCostante → (chiave → valore)
	 * </pre>
	 * Questo metodo NON rimuove la tabella dalla cache,
	 * ma elimina solo il record identificato dalla chiave
	 * all'interno della mappa interna della tabella.</p>
	 *
	 * @param nomeTabella	Tabella logica di riferimento nella cache
	 * @param id			ID del record da rimuovere
	 */
	public void removeRecordFromCachedTable(TabellaCostante nomeTabella, Integer id) {
		cache.computeIfPresent(nomeTabella, (k, mappaVecchia) -> {
			// Copia la mappa esistente
			Map<Integer, CacheableEntry> nuovaMappa = new HashMap<>(mappaVecchia);

			// Rimuove il record corrispondente all'ID
			nuovaMappa.remove(id);

			// Restituisce la mappa aggiornata come immutabile
			return Collections.unmodifiableMap(nuovaMappa);
		});
	}

	/*
	 * ===== METODI PRIVATI =====
	 */

	/**
	 * Inserisce l'intera mappa interna associata a una tabella logica nella cache.
	 *
	 * <p>
	 * Questo metodo viene utilizzato all'avvio dell'applicazione
	 * per caricare i dati iniziali dal database all'interno della cache centralizzata.
	 * </p>
	 *
	 * @param nomeTabella 			Tabella logica di riferimento nella cache
	 * @param mappa       			Mappa interna da associare alla tabella
	 * @throws NullPointerException se {@code nomeTabella} o {@code mappa} sono null
	 */
	protected void putTable(TabellaCostante nomeTabella, Map<Integer, ? extends CacheableEntry> mappa) {
		cache.put(nomeTabella, Collections.unmodifiableMap(mappa));
	}

	/**
	 * Restituisce l'intera mappa interna dei record per la tabella specificata.
	 *
	 * <p>Lancia un'eccezione se la tabella non è presente nella cache.</p>
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @return mappa interna immutabile (ID → record cacheabile)
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	private Map<Integer, ? extends CacheableEntry> getCachedRecords(TabellaCostante nomeTabella) {
		Map<Integer, ? extends CacheableEntry> map = cache.get(nomeTabella);

		if (map == null) {
			throw new IllegalArgumentException("Tipo di costante non trovato: " + nomeTabella);
		}

		return map;
	}
}
