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
import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Categoria;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.request.CategoriaReq;
import com.betacom.retrogames.service.interfaces.CategoriaService;

@Transactional
@SpringBootTest
public class CategoriaServiceTest {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private CategoriaRepository categoriaRepo;

	private CategoriaReq createReq(String nome) {
		CategoriaReq req = new CategoriaReq();
		req.setNome(normalizza(nome));
		req.setAttivo(true);
		return req;
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		String nome = "Action";
		categoriaRepo.findByNome(normalizza(nome)).ifPresent(categoriaRepo::delete);

		CategoriaReq req = createReq(nome);
		CategoriaDTO dto = categoriaService.crea(req);

		assertNotNull(dto);

		Integer id = dto.getId();
		assertNotNull(id);

		CategoriaDTO fetchedDto = categoriaService.getById(id);
		assertEquals(normalizza(nome), fetchedDto.getNome());
		assertTrue(fetchedDto.getAttivo());
	}

	@Test
	void testCreaFallito() throws AcademyException {
		String nome = "Adventure";
		categoriaRepo.findByNome(normalizza(nome)).ifPresent(categoriaRepo::delete);

		CategoriaReq req1 = createReq(nome);
		categoriaService.crea(req1);

		CategoriaReq req2 = createReq(nome);
		AcademyException exception = assertThrows(AcademyException.class, () -> categoriaService.crea(req2));
		assertTrue(exception.getMessage().contains("categoria-esistente"));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		String nome = "RPG";
		categoriaRepo.findByNome(normalizza(nome)).ifPresent(categoriaRepo::delete);

		CategoriaReq reqCreate = createReq(nome);
		CategoriaDTO createdDto = categoriaService.crea(reqCreate); // Now returns DTO
		Integer id = createdDto.getId();

		CachedCategoria cached = (CachedCategoria) cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, id);
		assertNotNull(cached);

		CategoriaReq reqUpdate = createReq("RPG Aggiornato");
		reqUpdate.setId(id);
		reqUpdate.setAttivo(false);
		categoriaService.aggiorna(reqUpdate);

		CategoriaDTO updatedDto = categoriaService.getById(id);
		assertEquals(normalizza("RPG Aggiornato"), updatedDto.getNome());
		assertFalse(updatedDto.getAttivo());

		CachedCategoria updatedCached = (CachedCategoria) cacheManager
				.getCachedEntryFromTable(TabellaCostante.CATEGORIA, id);
		assertNotNull(updatedCached);
		assertEquals(normalizza("RPG Aggiornato"), updatedCached.getNome());
		assertFalse(updatedCached.getAttivo());

		CategoriaReq reqUpdateSoloAttivo = new CategoriaReq();
		reqUpdateSoloAttivo.setId(id);
		reqUpdateSoloAttivo.setAttivo(true);
		categoriaService.aggiorna(reqUpdateSoloAttivo);

		CategoriaDTO dtoFinal = categoriaService.getById(id);
		assertEquals(normalizza("RPG Aggiornato"), dtoFinal.getNome());
		assertTrue(dtoFinal.getAttivo());
	}

	@Test
	void testAggiornaAttivoNull() throws Exception {
		Categoria categoria = new Categoria();
		categoria.setNome("CategoriaTest2");
		categoria.setAttivo(true);
		categoria = categoriaRepo.save(categoria);

		CachedCategoria cached = new CachedCategoria(categoria);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, cached);

		CategoriaReq req = createReq("CategoriaTest2 Aggiornato");
		req.setId(categoria.getId());
		req.setAttivo(null);

		categoriaService.aggiorna(req);

		Categoria aggiornata = categoriaRepo.findById(categoria.getId()).orElseThrow();
		assertEquals(true, aggiornata.getAttivo());
		assertEquals(normalizza("CategoriaTest2 Aggiornato"), aggiornata.getNome());
	}

	@Test
	void testAggiornaFallito() {
		CategoriaReq req = createReq("NonEsistente");
		req.setId(999);
		AcademyException exception = assertThrows(AcademyException.class, () -> categoriaService.aggiorna(req));
		assertTrue(exception.getMessage().contains("categoria-non-trovata"));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Test Categoria");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		CategoriaReq req = new CategoriaReq();
		req.setId(categoria.getId());

		categoriaService.disattiva(req);

		CategoriaDTO dto = categoriaService.getById(categoria.getId());
		assertFalse(dto.getAttivo());

		CachedCategoria cached = (CachedCategoria) cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA,
				categoria.getId());
		assertNotNull(cached);
		assertFalse(cached.getAttivo());
	}

	@Test
	void testDisattivaFallito() {
		CategoriaReq req = createReq("NonEsistente");
		req.setId(9999);
		AcademyException exception = assertThrows(AcademyException.class, () -> categoriaService.disattiva(req));
		assertTrue(exception.getMessage().contains("categoria-non-trovata"));
	}

	@Test
	void testGetByIdSuccesso() throws AcademyException {
		String nome = "Strategia";
		categoriaRepo.findByNome(normalizza(nome)).ifPresent(categoriaRepo::delete);

		CategoriaReq reqCreate = createReq(nome);
		CategoriaDTO createdDto = categoriaService.crea(reqCreate); // Now returns DTO
		Integer id = createdDto.getId();

		cacheManager.removeRecordFromCachedTable(TabellaCostante.CATEGORIA, id);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, id));

		CategoriaDTO dto = categoriaService.getById(id);
		assertNotNull(dto);
		assertEquals(normalizza(nome), dto.getNome());

		CachedCategoria cached = (CachedCategoria) cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, id);
		assertNotNull(cached);
		assertEquals(normalizza(nome), cached.getNome());
	}

	@Test
	void testGetByIdFallito() {
		Integer id = 9999;

		cacheManager.removeRecordFromCachedTable(TabellaCostante.CATEGORIA, id);
		categoriaRepo.findById(id).ifPresent(categoriaRepo::delete);

		AcademyException exception = assertThrows(AcademyException.class, () -> categoriaService.getById(id));
		assertTrue(exception.getMessage().contains("categoria-non-trovata"));
	}

	@Test
	void testListActiveSuccesso() {
		List<CategoriaDTO> lista = categoriaService.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(CategoriaDTO::getAttivo));
	}
}
