package com.betacom.retrogames.cache;

import static com.betacom.retrogames.util.Utils.normalizza;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.cache.implementations.CachedRuolo;
import com.betacom.retrogames.cache.implementations.CachedTipoMetodoPagamento;
import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.repository.TipoMetodoPagamentoRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CacheManager {

	/**
	 * Cache centralizzata per tutte le costanti dell'applicazione,
	 * utilizzata per validazioni e lookup rapidi senza accesso al database.
	 * 
	 * Struttura della cache:
	 *   - Chiave esterna: Identifica la tabella logica cacheata ({@link TabellaCostante})
	 *   - Valore esterno: Mappa interna immutabile dei record della tabella che associa:
	 *     - Chiave interna: Stringa normalizzata che identifica il record (Es. nome o codice)
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
	 *   - "Mappa interna" = Map<String, ? extends CacheableEntry>
	 *   - "Record / Oggetto" = istanza concreta che implementa CacheableEntry
	 *   - "Chiave" = stringa normalizzata usata per accedere ad un record nella mappa interna
	 */
	private final Map<TabellaCostante, Map<String, ? extends CacheableEntry>> cache = new ConcurrentHashMap<>();

	private final CategoriaRepository categoriaRepo;
	private final PiattaformaRepository piattaformaRepo;
	private final RuoloRepository ruoloRepo;
	private final TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo;

	public CacheManager(CategoriaRepository categoriaRepo, PiattaformaRepository piattaformaRepo,
			RuoloRepository ruoloRepo, TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo) {
		this.categoriaRepo = categoriaRepo;
		this.piattaformaRepo = piattaformaRepo;
		this.ruoloRepo = ruoloRepo;
		this.tipoMetodoPagamentoRepo = tipoMetodoPagamentoRepo;
	}

	/**
	 * Popola la cache all'avvio dell'applicazione
	 * caricando tutte le tabelle costanti dal database.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void loadCacheOnStartup() {
		cache.put(TabellaCostante.CATEGORIA,
				buildCacheMapFromList(categoriaRepo.findAll(), CachedCategoria::new, CachedCategoria::getNome));
		cache.put(TabellaCostante.PIATTAFORMA,
				buildCacheMapFromList(piattaformaRepo.findAll(), CachedPiattaforma::new, CachedPiattaforma::getCodice));
		cache.put(TabellaCostante.RUOLO,
				buildCacheMapFromList(ruoloRepo.findAll(), CachedRuolo::new, CachedRuolo::getNome));
		cache.put(TabellaCostante.TIPO_METODO_PAGAMENTO, buildCacheMapFromList(tipoMetodoPagamentoRepo.findAll(),
				CachedTipoMetodoPagamento::new, CachedTipoMetodoPagamento::getNome));

		log.debug("Cache caricata in memoria: {}", cache);
	}

	/*
	 * ===== METODI PER LA CACHE DELLE COSTANTI =====
	 */

	/**
	 * Verifica se la chiave è presente nella mappa interna della tabella specificata.
	 *
	 * <p>Normalizza la chiave prima del controllo. Se la tabella non è presente in cache,
	 * viene lanciata un'eccezione {@link IllegalArgumentException} tramite
	 * {@link #getCachedRecords(TabellaCostante)}.</p>
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @param chiave  					Chiave logica del record da verificare
	 * @return {@code true} se il record esiste nella cache, {@code false} altrimenti
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	public boolean isRecordCached(TabellaCostante nomeTabella, String chiave) {
		return getCachedRecords(nomeTabella).containsKey(normalizza(chiave));
	}

	/**
	 * Restituisce l'oggetto cacheable corrispondente a una chiave nella tabella specificata.
	 *
	 * <p>Normalizza la chiave prima di accedere alla mappa interna. Se la tabella non è presente
	 * nella cache, viene lanciata un'eccezione {@link IllegalArgumentException} tramite
	 * {@link #getCachedRecords(TabellaCostante)}.</p>
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @param chiave  					Chiave logica dell'oggetto cacheable da recuperare
	 * @return l'oggetto cacheable corrispondente alla chiave, o {@code null} se non presente
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	public CacheableEntry getCachedEntryFromTable(TabellaCostante nomeTabella, String chiave) {
		return getCachedRecords(nomeTabella).get(normalizza(chiave));
	}

	/**
	 * Restituisce l'ID di un record corrispondente a una chiave nella tabella specificata.
	 *
	 * <p>Questo metodo cerca un record nella mappa interna usando la chiave fornita
	 * (normalizzata) e, se presente, restituisce il suo ID.</p>
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @param chiave 					Chiave logica del record della mappa interna
	 * @return l'ID del record cacheato, o {@code null} se l'oggetto non esiste
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	public Integer getCachedRecordId(TabellaCostante nomeTabella, String chiave) {
		CacheableEntry entry = getCachedRecords(nomeTabella).get(normalizza(chiave));

		return (entry != null) ? entry.getId() : null;
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
	 * @param keyExtractor Funzione che estrae la chiave univoca (normalizzata) dal record,
	 *                     usata come chiave nella mappa interna
	 */
	public <T extends CacheableEntry> void addOrUpdateRecordInCachedTable(TabellaCostante nomeTabella, T record,
			Function<T, String> keyExtractor) {
		cache.compute(nomeTabella, (k, mappaVecchia) -> {
			Map<String, CacheableEntry> nuovaMappa = new HashMap<>();

			if (mappaVecchia != null) {
				// Copia gli elementi esistenti
				nuovaMappa.putAll(mappaVecchia);
			}

			// Calcola la chiave univoca normalizzata
			String key = normalizza(keyExtractor.apply(record));

			// Inserisce o aggiorna l’elemento
			nuovaMappa.put(key, record);

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
	 * @param chiave		Chiave logica del record da rimuovere (es. nome o codice)
	 */
	public void removeRecordFromCachedTable(TabellaCostante nomeTabella, String chiave) {
		cache.computeIfPresent(nomeTabella, (k, mappaVecchia) -> {
			// Copia la mappa esistente
			Map<String, CacheableEntry> nuovaMappa = new HashMap<>(mappaVecchia);

			// Rimuove il record corrispondente alla chiave normalizzata
			nuovaMappa.remove(normalizza(chiave));

			// Restituisce la mappa aggiornata come immutabile
			return Collections.unmodifiableMap(nuovaMappa);
		});
	}

	/*
	 * ===== METODI PRIVATI =====
	 */

	/**
	 * Costruisce una mappa immutabile di record partendo da una lista di entità.
	 *
	 * <p>Ogni elemento della lista viene prima trasformato tramite il {@code mapper},
	 * poi viene estratta una chiave univoca (normalizzata) con {@code keyExtractor}.
	 * La mappa risultante è resa immutabile e, in caso di chiavi duplicate,
	 * viene mantenuto il primo valore incontrato.</p>
	 *
	 * @param <E> 		   Tipo dell'entità JPA di origine
	 * @param <T>		   Tipo della tabella cacheabile (deve implementare {@link CacheableEntry})
	 * @param lista        Lista di entità JPA da trasformare
	 * @param mapper       Funzione che converte {@code E} in {@code T} (es. da entity JPA a DTO cacheabile)
	 * @param keyExtractor Funzione che estrae la chiave univoca (normalizzata) da {@code T}
	 * @return 			   Mappa immutabile (chiave → record cacheabile)
	 */
	private <E, T extends CacheableEntry> Map<String, T> buildCacheMapFromList(List<E> lista, Function<E, T> mapper,
			Function<T, String> keyExtractor) {
		/**
		 * Colleziona la lista in una mappa immutabile:
		 * - Normalizza la chiave
		 * - Mantiene il primo valore in caso di chiave duplicata: (a, b) → a
		 */
		Map<String, T> map = lista.stream().map(mapper)
				.collect(Collectors.toMap(t -> normalizza(keyExtractor.apply(t)), Function.identity(), (a, b) -> a));

		return Collections.unmodifiableMap(map);
	}

	/**
	 * Restituisce l'intera mappa interna dei record per la tabella specificata.
	 *
	 * <p>Lancia un'eccezione se la tabella non è presente nella cache.</p>
	 *
	 * @param nomeTabella 				Tabella logica di riferimento nella cache
	 * @return mappa interna immutabile (chiave → record cacheabile)
	 * @throws IllegalArgumentException se la tabella non è presente in cache
	 */
	private Map<String, ? extends CacheableEntry> getCachedRecords(TabellaCostante nomeTabella) {
		Map<String, ? extends CacheableEntry> map = cache.get(nomeTabella);

		if (map == null) {
			throw new IllegalArgumentException("Tipo di costante non trovato: " + nomeTabella);
		}

		return map;
	}
}
