package com.betacom.retrogames.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.retrogames.enums.TabellaCostante;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class CacheInitializerTest {

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
