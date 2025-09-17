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

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.service.interfaces.RuoloService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
public class RuoloControllerTest {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private RuoloService ruoloService;

	@Test
	void testCreaSuccesso() throws AcademyException {
		String nomeRuolo = "admin";

		RuoloReq req = new RuoloReq();
		req.setNome(normalizza(nomeRuolo));

		Integer id = ruoloService.crea(req);
		assertNotNull(id);

		RuoloDTO dto = ruoloService.getById(id);
		assertEquals(nomeRuolo, dto.getNome());
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		// Creazione ruolo se non esiste
		RuoloReq reqCreate = new RuoloReq();
		reqCreate.setNome("admin");
		Integer id = ruoloService.crea(reqCreate);
		RuoloDTO dto = ruoloService.getById(id);

		// Aggiornamento
		RuoloReq reqUpdate = new RuoloReq();
		reqUpdate.setId(dto.getId());
		reqUpdate.setNome("admin_aggiornato");
		ruoloService.aggiorna(reqUpdate);

		assertEquals("admin_aggiornato", ruoloService.getById(dto.getId()).getNome());
	}

	@Test
	void testAggiornaNonEsistente() {
		RuoloReq req = new RuoloReq();
		req.setId(999);
		req.setNome("FakeRole");

		assertThrows(AcademyException.class, () -> ruoloService.aggiorna(req));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		RuoloReq req = new RuoloReq();

		// Supponiamo che esista un ruolo con id 1
		req.setId(1);
		ruoloService.disattiva(req);

		// Verifico che il ruolo sia stato disattivato correttamente
		assertFalse(cacheManager.isRecordCached(TabellaCostante.RUOLO, req.getId()));
	}

	@Test
	void testDisattivaNonEsistenteLanciaEccezione() {
		RuoloReq req = new RuoloReq();

		// Id inesistente
		req.setId(99);
		assertThrows(AcademyException.class, () -> ruoloService.disattiva(req));
	}

	@Test
	void testListActive() {
		List<RuoloDTO> lista = ruoloService.listActive();

		// Verifico che la lista non sia vuota
		assertFalse(lista.isEmpty());

		// Verifico che tutti i ruoli siano attivi
		assertTrue(lista.stream().allMatch(RuoloDTO::getAttivo));
	}
}