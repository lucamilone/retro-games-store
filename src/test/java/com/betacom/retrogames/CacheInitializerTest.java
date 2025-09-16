package com.betacom.retrogames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.retrogames.cache.CacheInitializer;
import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.enums.TabellaCostante;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
class CacheInitializerTest {

	@Autowired
	private CacheInitializer cacheInitializer;

	@Autowired
	private CacheManager cacheManager;

	@Test
	void testReloadCache_PopulatesCacheCorrectly() {
		log.debug("Ricarico la cache manualmente per test");
		cacheInitializer.reloadCache();

		// Verifica record Categoria
		assertTrue(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, 1));
		assertEquals("console", cacheManager.getCachedRecordDisplay(TabellaCostante.CATEGORIA, 1));

		// Verifica record Piattaforma
		assertTrue(cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, 1));
		assertEquals("nes", cacheManager.getCachedRecordDisplay(TabellaCostante.PIATTAFORMA, 1));

		log.debug("Cache popolata correttamente dai repository H2");
	}
}
