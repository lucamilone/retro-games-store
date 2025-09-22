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
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Piattaforma;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.service.interfaces.PiattaformaService;

@Transactional
@SpringBootTest
public class PiattaformaServiceTest {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private PiattaformaService piattaformaService;

	@Autowired
	private PiattaformaRepository piattaformaRepo;

	private PiattaformaReq createReq(String nome, String codice, Integer anno) {
		PiattaformaReq req = new PiattaformaReq();
		req.setNome(nome);
		req.setCodice(normalizza(codice));
		req.setAnnoUscitaPiattaforma(anno);
		req.setAttivo(true);
		return req;
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		String nome = "PlayStation 1";
		String codice = "ps1";
		piattaformaRepo.findByCodice(normalizza(codice)).ifPresent(piattaformaRepo::delete);

		PiattaformaReq req = createReq(nome, codice, 1994);
		Integer id = piattaformaService.crea(req);

		assertNotNull(id);

		PiattaformaDTO dto = piattaformaService.getById(id);
		assertEquals(nome, dto.getNome());
		assertEquals(codice, dto.getCodice());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaFallito() throws AcademyException {
		String nome = "PlayStation 1";
		String codice = "ps1";
		piattaformaRepo.findByCodice(normalizza(codice)).ifPresent(piattaformaRepo::delete);

		PiattaformaReq req1 = createReq(nome, codice, 1994);
		piattaformaService.crea(req1);

		PiattaformaReq req2 = createReq(nome, codice, 1994);
		AcademyException exception = assertThrows(AcademyException.class, () -> piattaformaService.crea(req2));
		assertTrue(exception.getMessage().contains("piattaforma-esistente"));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		String codice = "ps2";
		piattaformaRepo.findByCodice(normalizza(codice)).ifPresent(piattaformaRepo::delete);

		PiattaformaReq reqCreate = createReq("PS2", codice, 2000);
		Integer id = piattaformaService.crea(reqCreate);

		Piattaforma piattaformaDB = piattaformaRepo.findById(id).get();
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, new CachedPiattaforma(piattaformaDB));

		PiattaformaReq reqUpdate = createReq("PS2 Aggiornata", codice, 2001);
		reqUpdate.setId(id);
		reqUpdate.setAttivo(false);
		piattaformaService.aggiorna(reqUpdate);

		PiattaformaDTO updatedDto = piattaformaService.getById(id);
		assertEquals("PS2 Aggiornata", updatedDto.getNome());
		assertEquals(normalizza(codice), updatedDto.getCodice());
		assertEquals(2001, updatedDto.getAnnoUscitaPiattaforma());
		assertFalse(updatedDto.getAttivo());

		CachedPiattaforma cached = (CachedPiattaforma) cacheManager.getCachedEntryFromTable(TabellaCostante.PIATTAFORMA,
				id);
		assertNotNull(cached);
		assertEquals("PS2 Aggiornata", cached.getNome());
		assertEquals(2001, cached.getAnnoUscitaPiattaforma());
		assertFalse(cached.getAttivo());

		PiattaformaReq reqUpdateSoloNome = new PiattaformaReq();
		reqUpdateSoloNome.setId(id);
		reqUpdateSoloNome.setNome("SoloNome");
		piattaformaService.aggiorna(reqUpdateSoloNome);
		assertEquals("SoloNome", piattaformaService.getById(id).getNome());

		PiattaformaReq reqUpdateSoloCodice = new PiattaformaReq();
		reqUpdateSoloCodice.setId(id);
		reqUpdateSoloCodice.setCodice("SoloCodice");
		piattaformaService.aggiorna(reqUpdateSoloCodice);
		assertEquals(normalizza("SoloCodice"), piattaformaService.getById(id).getCodice());

		PiattaformaReq reqUpdateSoloAnno = new PiattaformaReq();
		reqUpdateSoloAnno.setId(id);
		reqUpdateSoloAnno.setAnnoUscitaPiattaforma(2099);
		piattaformaService.aggiorna(reqUpdateSoloAnno);
		assertEquals(2099, piattaformaService.getById(id).getAnnoUscitaPiattaforma());

		PiattaformaReq reqUpdateSoloAttivo = new PiattaformaReq();
		reqUpdateSoloAttivo.setId(id);
		reqUpdateSoloAttivo.setAttivo(true);
		piattaformaService.aggiorna(reqUpdateSoloAttivo);
		assertTrue(piattaformaService.getById(id).getAttivo());
	}

	@Test
	void testAggiornaFallito() {
		PiattaformaReq req = createReq("NonEsistente", "nonex", 2000);
		req.setId(9999);

		AcademyException exception = assertThrows(AcademyException.class, () -> piattaformaService.aggiorna(req));
		assertTrue(exception.getMessage().contains("piattaforma-non-trovata"));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		Piattaforma piattaforma = new Piattaforma();
		piattaforma.setNome("Piattaforma di Test");
		piattaforma.setCodice("TP1");
		piattaforma.setAnnoUscitaPiattaforma(2000);
		piattaforma.setAttivo(true);
		piattaforma = piattaformaRepo.saveAndFlush(piattaforma);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, new CachedPiattaforma(piattaforma));

		PiattaformaReq req = new PiattaformaReq();
		req.setId(piattaforma.getId());
		piattaformaService.disattiva(req);

		PiattaformaDTO dto = piattaformaService.getById(piattaforma.getId());
		assertFalse(dto.getAttivo());

		CachedPiattaforma cached = (CachedPiattaforma) cacheManager.getCachedEntryFromTable(TabellaCostante.PIATTAFORMA,
				piattaforma.getId());
		assertNotNull(cached);
		assertFalse(cached.getAttivo());
	}

	@Test
	void testDisattivaFallito() {
		PiattaformaReq req = createReq("NonEsistente", "nonex", 2000);
		req.setId(9999);
		AcademyException exception = assertThrows(AcademyException.class, () -> piattaformaService.disattiva(req));
		assertTrue(exception.getMessage().contains("piattaforma-non-trovata"));
	}

	@Test
	void testGetByIdSuccesso() throws AcademyException {
		String nome = "GameCube";
		String codice = "gc";
		piattaformaRepo.findByCodice(normalizza(codice)).ifPresent(piattaformaRepo::delete);

		PiattaformaReq reqCreate = createReq(nome, codice, 2001);
		Integer id = piattaformaService.crea(reqCreate);

		cacheManager.removeRecordFromCachedTable(TabellaCostante.PIATTAFORMA, id);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, id));

		PiattaformaDTO dto = piattaformaService.getById(id);
		assertNotNull(dto);
		assertEquals(nome, dto.getNome());

		CachedPiattaforma cached = (CachedPiattaforma) cacheManager.getCachedEntryFromTable(TabellaCostante.PIATTAFORMA,
				id);
		assertNotNull(cached);
		assertEquals(nome, cached.getNome());
	}

	@Test
	void testGetByIdFallito() {
		Integer idInesistente = 9999;
		cacheManager.removeRecordFromCachedTable(TabellaCostante.PIATTAFORMA, idInesistente);
		piattaformaRepo.findById(idInesistente).ifPresent(piattaformaRepo::delete);

		AcademyException ex = assertThrows(AcademyException.class, () -> piattaformaService.getById(idInesistente));
		assertTrue(ex.getMessage().contains("piattaforma-non-trovata"));
	}

	@Test
	void testListActiveSuccesso() throws AcademyException {
		String nome1 = "PS Active 1";
		String nome2 = "PS Active 2";
		String codice1 = "psa1";
		String codice2 = "psa2";

		piattaformaRepo.findByCodice(normalizza(codice1)).ifPresent(piattaformaRepo::delete);
		piattaformaRepo.findByCodice(normalizza(codice2)).ifPresent(piattaformaRepo::delete);

		piattaformaService.crea(createReq(nome1, codice1, 2000));
		piattaformaService.crea(createReq(nome2, codice2, 2001));

		List<PiattaformaDTO> lista = piattaformaService.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(PiattaformaDTO::getAttivo));
	}
}
