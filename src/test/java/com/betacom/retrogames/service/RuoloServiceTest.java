package com.betacom.retrogames.service;

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
import com.betacom.retrogames.model.Ruolo;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.service.interfaces.RuoloService;

@Transactional
@SpringBootTest
public class RuoloServiceTest {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private RuoloService ruoloService;

	@Autowired
	private RuoloRepository ruoloRepo;

	private RuoloReq createReq(String nome) {
		RuoloReq req = new RuoloReq();
		req.setNome(normalizza(nome));
		return req;
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		String nomeRuolo = "admin";
		ruoloRepo.findByNome(normalizza(nomeRuolo)).ifPresent(ruoloRepo::delete);

		RuoloReq req = createReq(nomeRuolo);
		Integer id = ruoloService.crea(req);

		assertNotNull(id);
		RuoloDTO dto = ruoloService.getById(id);
		assertEquals(nomeRuolo, dto.getNome());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaFallito() throws AcademyException {
		String nomeRuolo = "admin";
		ruoloRepo.findByNome(normalizza(nomeRuolo)).ifPresent(ruoloRepo::delete);

		RuoloReq req1 = createReq(nomeRuolo);
		RuoloReq req2 = createReq(nomeRuolo);

		ruoloService.crea(req1);

		AcademyException exception = assertThrows(AcademyException.class, () -> ruoloService.crea(req2));
		assertTrue(exception.getMessage().contains("ruolo-esistente"));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		String nomeRuolo = "ruolo_test_completo";
		ruoloRepo.findByNome(normalizza(nomeRuolo)).ifPresent(ruoloRepo::delete);

		RuoloReq reqCreate = createReq(nomeRuolo);
		Integer id = ruoloService.crea(reqCreate);

		Ruolo ruoloDB = ruoloRepo.findById(id).get();
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.RUOLO, new CachedRuolo(ruoloDB));

		RuoloDTO dtoInitial = ruoloService.getById(id);
		assertTrue(dtoInitial.getAttivo());
		assertEquals(normalizza(nomeRuolo), dtoInitial.getNome());

		String nomeAggiornato = "ruolo_test_aggiornato";
		RuoloReq reqUpdate = createReq(nomeAggiornato);
		reqUpdate.setId(id);
		reqUpdate.setAttivo(false);
		ruoloService.aggiorna(reqUpdate);

		RuoloDTO updatedDto = ruoloService.getById(id);
		assertEquals(normalizza(nomeAggiornato), updatedDto.getNome());
		assertFalse(updatedDto.getAttivo());

		CachedRuolo cached = (CachedRuolo) cacheManager.getCachedEntryFromTable(TabellaCostante.RUOLO, id);
		assertNotNull(cached);
		assertEquals(normalizza(nomeAggiornato), cached.getNome());
		assertFalse(cached.getAttivo());

		RuoloReq reqUpdateSoloAttivo = new RuoloReq();
		reqUpdateSoloAttivo.setId(id);
		reqUpdateSoloAttivo.setAttivo(true);
		ruoloService.aggiorna(reqUpdateSoloAttivo);

		RuoloDTO dtoFinal = ruoloService.getById(id);
		assertEquals(normalizza(nomeAggiornato), dtoFinal.getNome());
		assertTrue(dtoFinal.getAttivo());
	}

	@Test
	void testAggiornaFallito() {
		RuoloReq req = createReq("NonEsistente");
		req.setId(999);
		AcademyException exception = assertThrows(AcademyException.class, () -> ruoloService.aggiorna(req));
		assertTrue(exception.getMessage().contains("ruolo-non-trovato"));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		Ruolo ruolo = new Ruolo();
		ruolo.setNome("Ruolo di Test");
		ruolo.setAttivo(true);
		ruolo = ruoloRepo.save(ruolo);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.RUOLO, new CachedRuolo(ruolo));

		RuoloReq req = new RuoloReq();
		req.setId(ruolo.getId());

		ruoloService.disattiva(req);

		assertFalse(ruolo.getAttivo());
	}

	@Test
	void testDisattivaFallito() {
		RuoloReq req = createReq("NonEsistente");
		req.setId(9999);
		AcademyException exception = assertThrows(AcademyException.class, () -> ruoloService.disattiva(req));
		assertTrue(exception.getMessage().contains("ruolo-non-trovato"));
	}

	@Test
	void testGetByIdSuccesso() throws AcademyException {
		String nomeRuolo = "admin_cache_getbyid_update";
		ruoloRepo.findByNome(normalizza(nomeRuolo)).ifPresent(ruoloRepo::delete);

		RuoloReq reqCreate = createReq(nomeRuolo);
		Integer id = ruoloService.crea(reqCreate);

		cacheManager.removeRecordFromCachedTable(TabellaCostante.RUOLO, id);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.RUOLO, id));

		RuoloDTO dto = ruoloService.getById(id);
		assertNotNull(dto);
		assertEquals(normalizza(nomeRuolo), dto.getNome());

		CachedRuolo cached = (CachedRuolo) cacheManager.getCachedEntryFromTable(TabellaCostante.RUOLO, id);
		assertNotNull(cached);
		assertEquals(normalizza(nomeRuolo), cached.getNome());
	}

	@Test
	void testListActiveSuccesso() throws AcademyException {
		String nomeRuolo1 = "admin_active_1";
		String nomeRuolo2 = "admin_active_2";

		ruoloRepo.findByNome(normalizza(nomeRuolo1)).ifPresent(ruoloRepo::delete);
		ruoloRepo.findByNome(normalizza(nomeRuolo2)).ifPresent(ruoloRepo::delete);

		ruoloService.crea(createReq(nomeRuolo1));
		ruoloService.crea(createReq(nomeRuolo2));

		List<RuoloDTO> lista = ruoloService.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(RuoloDTO::getAttivo));
	}
}
