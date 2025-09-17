package com.betacom.retrogames;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedRuolo;
import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.service.interfaces.RuoloService;

@SpringBootTest
@Transactional
class RuoloServiceTest {
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private RuoloService ruoloService;

	private RuoloReq createValidRuoloReq(String nome) {
		RuoloReq req = new RuoloReq();
		req.setNome(normalizza(nome));
		return req;
	}

	private RuoloReq createValidRuoloReqWithId(String nome, Integer id) {
		RuoloReq req = new RuoloReq();
		req.setNome(normalizza(nome));
		req.setId(id);
		return req;
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		String nomeRuolo = "admin";
		RuoloReq req = createValidRuoloReq(nomeRuolo);

		Integer id = ruoloService.crea(req);

		assertNotNull(id);
		RuoloDTO dto = ruoloService.getById(id);
		assertEquals(nomeRuolo, dto.getNome());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaRuoloGiàEsistente() throws AcademyException {
		String nomeRuolo = "admin";
		RuoloReq req1 = createValidRuoloReq(nomeRuolo);
		RuoloReq req2 = createValidRuoloReq(nomeRuolo);

		ruoloService.crea(req1);
		AcademyException exception = assertThrows(AcademyException.class, () -> ruoloService.crea(req2));
		assertTrue(exception.getMessage().contains("ruolo-esistente"));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		String nomeRuolo = "admin";
		RuoloReq reqCreate = createValidRuoloReq(nomeRuolo);
		Integer id = ruoloService.crea(reqCreate);

		RuoloDTO dto = ruoloService.getById(id);
		String updatedName = "admin_aggiornato";

		RuoloReq reqUpdate = createValidRuoloReqWithId(updatedName, dto.getId());
		ruoloService.aggiorna(reqUpdate);

		RuoloDTO updatedDto = ruoloService.getById(dto.getId());
		assertEquals(updatedName, updatedDto.getNome());
	}

	@Test
	void testAggiornaRuoloNonEsistente() {
		RuoloReq req = createValidRuoloReqWithId("NonEsistente", 999);
		AcademyException exception = assertThrows(AcademyException.class, () -> ruoloService.aggiorna(req));
		assertTrue(exception.getMessage().contains("ruolo-non-trovato"));
	}

	@Test
	void testAggiornaSetAttivo() throws AcademyException {
		// Creo un ruolo inizialmente attivo
		String nomeRuolo = "ruolo_da_disattivare";
		RuoloReq reqCreate = createValidRuoloReq(nomeRuolo);
		Integer id = ruoloService.crea(reqCreate);

		// Verifico che sia attivo all'inizio
		RuoloDTO dtoInitial = ruoloService.getById(id);
		assertTrue(dtoInitial.getAttivo());

		// Aggiorno il ruolo impostando attivo a false
		RuoloReq reqUpdate = createValidRuoloReqWithId(nomeRuolo, id);
		reqUpdate.setAttivo(false); // qui settiamo lo stato attivo
		ruoloService.aggiorna(reqUpdate);

		// Verifico che il ruolo sia stato aggiornato correttamente
		RuoloDTO updatedDto = ruoloService.getById(id);
		assertFalse(updatedDto.getAttivo());

		// Verifico anche la cache
		assertTrue(cacheManager.isRecordCached(TabellaCostante.RUOLO, id));
		CachedRuolo cached = (CachedRuolo) cacheManager.getCachedEntryFromTable(TabellaCostante.RUOLO, id);
		assertNotNull(cached);
		assertFalse(cached.getAttivo());
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		String nomeRuolo = "admin_to_disable";
		RuoloReq reqCreate = createValidRuoloReq(nomeRuolo);
		Integer id = ruoloService.crea(reqCreate);

		RuoloReq reqDisable = createValidRuoloReqWithId(nomeRuolo, id);
		ruoloService.disattiva(reqDisable);

		assertFalse(cacheManager.isRecordCached(TabellaCostante.RUOLO, id));
	}

	@Test
	void testDisattivaRuoloNonEsistente() {
		RuoloReq req = createValidRuoloReqWithId("NonEsistente", 9999);
		AcademyException exception = assertThrows(AcademyException.class, () -> ruoloService.disattiva(req));
		assertTrue(exception.getMessage().contains("ruolo-non-trovato"));
	}

	@Test
	void testGetByIdUpdatesCacheWhenNotPresent() throws AcademyException {
		// Creo un ruolo
		String nomeRuolo = "admin_cache_getbyid_update";
		RuoloReq reqCreate = createValidRuoloReq(nomeRuolo);
		Integer id = ruoloService.crea(reqCreate);

		// Rimuovo il record dalla cache per simulare la situazione "non presente"
		cacheManager.removeRecordFromCachedTable(TabellaCostante.RUOLO, id);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.RUOLO, id));

		// Chiamo getById: dovrebbe leggere dal DB e aggiornare la cache
		RuoloDTO dto = ruoloService.getById(id);
		assertNotNull(dto);
		assertEquals(normalizza(nomeRuolo), dto.getNome());

		// Verifico che la cache sia stata aggiornata
		assertTrue(cacheManager.isRecordCached(TabellaCostante.RUOLO, id));
		CachedRuolo cached = (CachedRuolo) cacheManager.getCachedEntryFromTable(TabellaCostante.RUOLO, id);
		assertNotNull(cached);
		assertEquals(normalizza(nomeRuolo), cached.getNome());
	}

	@Test
	void testListActive() throws AcademyException {
		String nomeRuolo1 = "admin_active_1";
		String nomeRuolo2 = "admin_active_2";

		RuoloReq req1 = createValidRuoloReq(nomeRuolo1);
		RuoloReq req2 = createValidRuoloReq(nomeRuolo2);

		ruoloService.crea(req1);
		ruoloService.crea(req2);

		List<RuoloDTO> lista = ruoloService.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(RuoloDTO::getAttivo));
	}
}