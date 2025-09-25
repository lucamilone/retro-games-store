package com.betacom.retrogames.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.enums.TabellaCostante;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
public class CacheManagerTest {

	@Autowired
	private CacheManager cacheManager;

	@Test
	void testIsRecordCachedSuccesso() {
		log.debug("Verifico che i record esistenti siano in cache");
		assertTrue(cacheManager.isRecordCached(TabellaCostante.RUOLO, 1));
		assertTrue(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 1));
		assertTrue(cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, 1));
		log.debug("Record esistenti correttamente rilevati nella cache");
	}

	@Test
	void testIsRecordCachedFallito() {
		log.debug("Verifico che gli ID non presenti restituiscano false");
		assertFalse(cacheManager.isRecordCached(TabellaCostante.RUOLO, 9999));
		assertFalse(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 9999));
		log.debug("ID non presenti correttamente rilevati come assenti");
	}

	@Test
	void testGetCachedEntryFromTableSuccesso() {
		log.debug("Recupero un record esistente dalla cache");

		// Recupero la mappa dalla cache
		Map<Integer, ? extends CacheableEntry> categoriaMap = cacheManager.getCachedRecords(TabellaCostante.CATEGORIA);

		Integer existingId = categoriaMap.keySet().iterator().next();
		CacheableEntry expectedEntry = categoriaMap.get(existingId);

		CacheableEntry entry = cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, existingId);

		assertNotNull(entry, "Il record non deve essere null");
		assertEquals(expectedEntry.getNome(), entry.getNome(),
				"Il nome deve corrispondere al record presente in cache");

		log.debug("Record '{}' recuperato correttamente", entry.getNome());
	}

	@Test
	void testGetCachedEntryFromTableFallito() {
		log.debug("Provo a recuperare un record inesistente -> dovrebbe ritornare null");
		CacheableEntry entry = cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, 9999);
		assertNull(entry);
		log.debug("Record inesistente restituisce null come previsto");
	}

	@Test
	void testGetCachedRecordDisplay_Categoria() {
		log.debug("Verifico la visualizzazione dei record per CATEGORIA");

		Map<Integer, ? extends CacheableEntry> categoriaMap = cacheManager.getCachedRecords(TabellaCostante.CATEGORIA);

		if (!categoriaMap.isEmpty()) {
			Integer existingId = categoriaMap.keySet().iterator().next();
			CacheableEntry entry = categoriaMap.get(existingId);
			String display = cacheManager.getCachedRecordDisplay(TabellaCostante.CATEGORIA, existingId);
			assertEquals(entry.getNome(), display, "Il nome visualizzato deve corrispondere al record esistente");
		}

		// Verifica ID inesistente
		String nullValue = cacheManager.getCachedRecordDisplay(TabellaCostante.CATEGORIA, -1);
		assertNull(nullValue, "ID inesistente deve restituire null");

		log.debug("Test CATEGORIA completato");
	}

	@Test
	void testGetCachedRecordDisplay_Piattaforma() {
		log.debug("Verifico la visualizzazione dei record per PIATTAFORMA");

		Map<Integer, ? extends CacheableEntry> piattaformaMap = cacheManager
				.getCachedRecords(TabellaCostante.PIATTAFORMA);

		if (!piattaformaMap.isEmpty()) {
			Integer existingId = piattaformaMap.keySet().iterator().next();
			CacheableEntry entry = piattaformaMap.get(existingId);
			String display = cacheManager.getCachedRecordDisplay(TabellaCostante.PIATTAFORMA, existingId);
			assertEquals(((CachedPiattaforma) entry).getCodice(), display,
					"Il codice visualizzato deve corrispondere al record esistente");
		}

		// Verifica ID inesistente
		String nullValue = cacheManager.getCachedRecordDisplay(TabellaCostante.PIATTAFORMA, -1);
		assertNull(nullValue, "ID inesistente deve restituire null");

		log.debug("Test PIATTAFORMA completato");
	}

	@Test
	void testAddOrUpdateRecordInCachedTableSuccesso() {
		log.debug("Aggiungo e aggiorno un record di test nella cache");

		CacheableEntry record = new CachedCategoria(999, "TestCategoria", true);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, record);
		assertTrue(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 999));
		assertEquals("TestCategoria", cacheManager.getCachedRecordDisplay(TabellaCostante.CATEGORIA, 999));

		// Aggiornamento
		record = new CachedCategoria(999, "UpdatedCategoria", true);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, record);
		assertEquals("UpdatedCategoria", cacheManager.getCachedRecordDisplay(TabellaCostante.CATEGORIA, 999));

		log.debug("Record aggiunto e aggiornato correttamente");
	}

	@Test
	void testAddOrUpdateRecordInCachedTableFallito() {
		log.debug("Verifico il ramo con mappaVecchia == null");

		// Creo un nuovo CacheManager "vuoto" senza popolamento iniziale
		CacheManager testCacheManager = new CacheManager();

		// Inserisco un record in una tabella che parte vuota: mappaVecchia == null
		CacheableEntry record = new CachedCategoria(9999, "TestCategoriaNull", true);

		testCacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, record);

		// Controllo che il record sia stato inserito correttamente
		Map<Integer, ? extends CacheableEntry> map = testCacheManager.getCachedRecords(TabellaCostante.CATEGORIA);
		assertEquals("TestCategoriaNull", map.get(9999).getNome());

		log.debug("Ramo con mappaVecchia == null eseguito correttamente");
	}

	@Test
	void testRemoveRecordFromCachedTableSuccesso() {
		log.debug("Aggiungo e poi rimuovo un record dalla cache");

		CacheableEntry record = new CachedCategoria(1000, "ToRemove", true);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, record);

		cacheManager.removeRecordFromCachedTable(TabellaCostante.CATEGORIA, 1000);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 1000));

		log.debug("Record rimosso correttamente dalla cache");
	}

	@Test
	void testGetCachedRecordsSuccesso() {
		log.debug("Verifico getCachedRecords con tabella esistente");

		// Aggiungo un record reale
		CacheableEntry record = new CachedCategoria(1, "Categoria1", true);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, record);

		// Recupero la mappa tramite il metodo
		Map<Integer, ? extends CacheableEntry> result = cacheManager.getCachedRecords(TabellaCostante.CATEGORIA);

		assertNotNull(result, "La mappa restituita non deve essere null");
		assertTrue(result.containsKey(1), "La mappa deve contenere il record appena aggiunto");
		assertEquals("Categoria1", result.get(1).getNome(), "Il nome del record deve corrispondere");

		log.debug("Test successo completato correttamente");
	}

	@Test
	void testGetCachedRecordsFallito() {
		log.debug("Verifico getCachedRecords con tabella inesistente");

		// Creo un nuovo CacheManager "vuoto" senza popolamento iniziale
		CacheManager testCacheManager = new CacheManager();

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> testCacheManager.getCachedRecords(TabellaCostante.CATEGORIA),
				"Deve lanciare IllegalArgumentException se la mappa è null");

		assertTrue(exception.getMessage().toLowerCase().contains("non trovato"));

		log.debug("Test fallimento completato correttamente");
	}
}
