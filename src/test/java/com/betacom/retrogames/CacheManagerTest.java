package com.betacom.retrogames;

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

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.cache.interfaces.CacheableEntry;
import com.betacom.retrogames.enums.TabellaCostante;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
public class CacheManagerTest {

	@Autowired
	private CacheManager cacheManager;

	@Test
	void testIsRecordCached_Existing() {
		log.debug("Verifico che i record esistenti siano in cache");
		assertTrue(cacheManager.isRecordCached(TabellaCostante.RUOLO, 1));
		assertTrue(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 1));
		assertTrue(cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, 1));
		log.debug("Record esistenti correttamente rilevati nella cache");
	}

	@Test
	void testIsRecordCached_NonExisting() {
		log.debug("Verifico che gli ID non presenti restituiscano false");
		assertFalse(cacheManager.isRecordCached(TabellaCostante.RUOLO, 9999));
		assertFalse(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 9999));
		log.debug("ID non presenti correttamente rilevati come assenti");
	}

	@Test
	void testIsRecordCached_TableNotExist_Throws() {
		log.debug("Verifico che l'accesso a una tabella inesistente lanci IllegalArgumentException");

		// Sottoclasse di CacheManager per il test
		CacheManager testCacheManager = new CacheManager() {
			@Override
			protected Map<Integer, ? extends CacheableEntry> getCachedRecords(TabellaCostante nomeTabella) {
				// Simulo che la tabella CATEGORIA non sia presente
				if (nomeTabella == TabellaCostante.CATEGORIA) {
					throw new IllegalArgumentException("Tipo di costante non trovato: " + nomeTabella);
				}
				return super.getCachedRecords(nomeTabella);
			}
		};

		// Il test verifica che venga lanciata IllegalArgumentException
		assertThrows(IllegalArgumentException.class,
				() -> testCacheManager.isRecordCached(TabellaCostante.CATEGORIA, 1));

		log.debug("Eccezione lanciata correttamente per tabella inesistente");
	}

	@Test
	void testGetCachedEntryFromTable_Existing() {
		log.debug("Recupero un record esistente dalla cache");
		CacheableEntry entry = cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, 1);
		assertNotNull(entry);
		assertEquals("console", entry.getNome());
		log.debug("Record '{}' recuperato correttamente", entry.getNome());
	}

	@Test
	void testGetCachedEntryFromTable_NonExisting() {
		log.debug("Provo a recuperare un record inesistente → dovrebbe ritornare null");
		CacheableEntry entry = cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, 9999);
		assertNull(entry);
		log.debug("Record inesistente restituisce null come previsto");
	}

	@Test
	void testGetCachedRecordDisplay() {
		log.debug("Verifico la visualizzazione dei record per Categoria e Piattaforma");

		String nomeCategoria = cacheManager.getCachedRecordDisplay(TabellaCostante.CATEGORIA, 1);
		assertEquals("console", nomeCategoria);

		String codicePiattaforma = cacheManager.getCachedRecordDisplay(TabellaCostante.PIATTAFORMA, 1);
		assertEquals("nes", codicePiattaforma);

		String nullValue = cacheManager.getCachedRecordDisplay(TabellaCostante.CATEGORIA, 9999);
		assertNull(nullValue);

		log.debug("Visualizzazioni dei record verificate correttamente");
	}

	@Test
	void testAddOrUpdateRecordInCachedTable() {
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
	void testRemoveRecordFromCachedTable() {
		log.debug("Aggiungo e poi rimuovo un record dalla cache");

		CacheableEntry record = new CachedCategoria(1000, "ToRemove", true);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, record);

		cacheManager.removeRecordFromCachedTable(TabellaCostante.CATEGORIA, 1000);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 1000));

		log.debug("Record rimosso correttamente dalla cache");
	}
}
