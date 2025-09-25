package com.betacom.retrogames.cache;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CacheInitializer {

	private final CacheManager cacheManager;
	private final CategoriaRepository categoriaRepo;
	private final PiattaformaRepository piattaformaRepo;
	private final RuoloRepository ruoloRepo;
	private final TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo;

	public CacheInitializer(CacheManager cacheManager, CategoriaRepository categoriaRepo,
			PiattaformaRepository piattaformaRepo, RuoloRepository ruoloRepo,
			TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo) {
		this.cacheManager = cacheManager;
		this.categoriaRepo = categoriaRepo;
		this.piattaformaRepo = piattaformaRepo;
		this.ruoloRepo = ruoloRepo;
		this.tipoMetodoPagamentoRepo = tipoMetodoPagamentoRepo;
	}

	/**
	 * Popola la cache all'avvio dell'applicazione caricando tutte le tabelle costanti dal database.
	 * Questo metodo viene eseguito automaticamente dopo che il contesto Spring è pronto.
	 */
	@EventListener(ApplicationReadyEvent.class)
	private void loadCacheOnStartup() {
		loadCache();
		log.debug("Cache inizializzata all'avvio dell'applicazione");
	}

	/**
	 * Carica in memoria tutte le tabelle costanti dal database e le inserisce nella cache.
	 * Questo metodo viene utilizzato sia all'avvio dell'applicazione che durante il reload manuale della cache.
	 */
	private void loadCache() {
		cacheManager.putTable(TabellaCostante.CATEGORIA,
				buildCacheMapFromList(categoriaRepo.findAll(), CachedCategoria::new));
		cacheManager.putTable(TabellaCostante.PIATTAFORMA,
				buildCacheMapFromList(piattaformaRepo.findAll(), CachedPiattaforma::new));
		cacheManager.putTable(TabellaCostante.RUOLO, buildCacheMapFromList(ruoloRepo.findAll(), CachedRuolo::new));
		cacheManager.putTable(TabellaCostante.TIPO_METODO_PAGAMENTO,
				buildCacheMapFromList(tipoMetodoPagamentoRepo.findAll(), CachedTipoMetodoPagamento::new));
	}

	/**
	 * Ricarica la cache in memoria, aggiornando i dati delle tabelle costanti
	 * con quelli correnti presenti nel database.
	 * Questo metodo può essere chiamato manualmente in qualsiasi momento per sincronizzare la cache.
	 */
	public void reloadCache() {
		loadCache();
		log.debug("Cache ricaricata manualmente in memoria");
	}

	/**
	 * Costruisce una mappa immutabile di record partendo da una lista di entità.
	 *
	 * <p>Ogni elemento della lista viene trasformato tramite il {@code mapper}.
	 * La mappa risultante è resa immutabile e usa l'ID del record come chiave.</p>
	 *
	 * @param <E> 		   Tipo dell'entità JPA di origine
	 * @param <T>		   Tipo della tabella cacheabile (deve implementare {@link CacheableEntry})
	 * @param lista        Lista di entità JPA da trasformare
	 * @param mapper       Funzione che converte {@code E} in {@code T} (es. da entity JPA a DTO cacheabile)
	 * @return        	   Mappa immutabile (ID → record cacheabile)
	 */
	private <E, T extends CacheableEntry> Map<Integer, T> buildCacheMapFromList(List<E> lista, Function<E, T> mapper) {
		return Collections
				.unmodifiableMap(lista.stream().map(mapper).collect(Collectors.toMap(T::getId, Function.identity())));
	}
}
