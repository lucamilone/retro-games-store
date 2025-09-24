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
import com.betacom.retrogames.cache.implementations.CachedTipoMetodoPagamento;
import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.TipoMetodoPagamento;
import com.betacom.retrogames.repository.TipoMetodoPagamentoRepository;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;
import com.betacom.retrogames.service.interfaces.TipoMetodoPagamentoService;

@Transactional
@SpringBootTest
public class TipoMetodoPagamentoServiceTest {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private TipoMetodoPagamentoService tipoMetodoPagamentoService;

	@Autowired
	private TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo;

	private TipoMetodoPagamentoReq createReq(String nome) {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setNome(normalizza(nome));
		req.setAttivo(true);
		return req;
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		String nome = "crypto";
		tipoMetodoPagamentoRepo.findByNome(normalizza(nome)).ifPresent(tipoMetodoPagamentoRepo::delete);

		TipoMetodoPagamentoReq req = createReq(nome);
		TipoMetodoPagamentoDTO dto = tipoMetodoPagamentoService.crea(req);

		assertNotNull(dto);
		assertNotNull(dto.getId());
		assertEquals(normalizza(nome), dto.getNome());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaFallito() throws AcademyException {
		String nome = "paypal";
		tipoMetodoPagamentoRepo.findByNome(normalizza(nome)).ifPresent(tipoMetodoPagamentoRepo::delete);

		TipoMetodoPagamentoReq req1 = createReq(nome);
		tipoMetodoPagamentoService.crea(req1);

		TipoMetodoPagamentoReq req2 = createReq(nome);
		AcademyException exception = assertThrows(AcademyException.class, () -> tipoMetodoPagamentoService.crea(req2));
		assertTrue(exception.getMessage().contains("tipo-metodo-pagamento-esistente"));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		String nome = "Bonifico";
		tipoMetodoPagamentoRepo.findByNome(normalizza(nome)).ifPresent(tipoMetodoPagamentoRepo::delete);

		TipoMetodoPagamentoReq reqCreate = createReq(nome);
		TipoMetodoPagamentoDTO createdDto = tipoMetodoPagamentoService.crea(reqCreate);
		Integer id = createdDto.getId();

		TipoMetodoPagamento tipo = tipoMetodoPagamentoRepo.findById(id).get();
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO,
				new CachedTipoMetodoPagamento(tipo));

		TipoMetodoPagamentoReq reqUpdateAttivo = new TipoMetodoPagamentoReq();
		reqUpdateAttivo.setId(id);
		reqUpdateAttivo.setAttivo(false);
		tipoMetodoPagamentoService.aggiorna(reqUpdateAttivo);

		TipoMetodoPagamentoDTO dto1 = tipoMetodoPagamentoService.getById(id);
		assertEquals(normalizza(nome), dto1.getNome());
		assertFalse(dto1.getAttivo());

		TipoMetodoPagamentoReq reqUpdateNome = new TipoMetodoPagamentoReq();
		reqUpdateNome.setId(id);
		reqUpdateNome.setNome("Bonifico Aggiornato");
		tipoMetodoPagamentoService.aggiorna(reqUpdateNome);

		TipoMetodoPagamentoDTO dto2 = tipoMetodoPagamentoService.getById(id);
		assertEquals(normalizza("Bonifico Aggiornato"), dto2.getNome());
		assertFalse(dto2.getAttivo());
	}

	@Test
	void testAggiornaFallito() {
		TipoMetodoPagamentoReq req = createReq("NonEsistente");
		req.setId(9999);
		AcademyException exception = assertThrows(AcademyException.class,
				() -> tipoMetodoPagamentoService.aggiorna(req));
		assertTrue(exception.getMessage().contains("tipo-metodo-pagamento-non-trovato"));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		TipoMetodoPagamento tipo = new TipoMetodoPagamento();
		tipo.setNome("disattiva_test");
		tipo.setAttivo(true);
		tipo = tipoMetodoPagamentoRepo.save(tipo);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO,
				new CachedTipoMetodoPagamento(tipo));

		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setId(tipo.getId());

		tipoMetodoPagamentoService.disattiva(req);

		TipoMetodoPagamentoDTO dto = tipoMetodoPagamentoService.getById(tipo.getId());
		assertFalse(dto.getAttivo());

		CachedTipoMetodoPagamento cached = (CachedTipoMetodoPagamento) cacheManager
				.getCachedEntryFromTable(TabellaCostante.TIPO_METODO_PAGAMENTO, tipo.getId());
		assertNotNull(cached);
		assertFalse(cached.getAttivo());
	}

	@Test
	void testDisattivaFallito() {
		TipoMetodoPagamentoReq req = createReq("NonEsistente");
		req.setId(9999);
		AcademyException exception = assertThrows(AcademyException.class,
				() -> tipoMetodoPagamentoService.disattiva(req));
		assertTrue(exception.getMessage().contains("tipo-metodo-pagamento-non-trovato"));
	}

	@Test
	void testGetByIdSuccesso() throws AcademyException {
		String nome = "crypto_cache_getbyid";
		tipoMetodoPagamentoRepo.findByNome(normalizza(nome)).ifPresent(tipoMetodoPagamentoRepo::delete);

		TipoMetodoPagamentoReq reqCreate = createReq(nome);
		TipoMetodoPagamentoDTO createdDto = tipoMetodoPagamentoService.crea(reqCreate);
		Integer id = createdDto.getId();

		cacheManager.removeRecordFromCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO, id);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.TIPO_METODO_PAGAMENTO, id));

		TipoMetodoPagamentoDTO dto = tipoMetodoPagamentoService.getById(id);
		assertNotNull(dto);
		assertEquals(normalizza(nome), dto.getNome());

		CachedTipoMetodoPagamento cached = (CachedTipoMetodoPagamento) cacheManager
				.getCachedEntryFromTable(TabellaCostante.TIPO_METODO_PAGAMENTO, id);
		assertNotNull(cached);
		assertEquals(normalizza(nome), cached.getNome());
	}

	@Test
	void testGetByIdFallito() {
		Integer id = 9999;

		cacheManager.removeRecordFromCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO, id);
		tipoMetodoPagamentoRepo.findById(id).ifPresent(tipoMetodoPagamentoRepo::delete);

		AcademyException exception = assertThrows(AcademyException.class, () -> tipoMetodoPagamentoService.getById(id));
		assertTrue(exception.getMessage().contains("tipo-metodo-pagamento-non-trovato"));
	}

	@Test
	void testListActiveSuccesso() throws AcademyException {
		TipoMetodoPagamento tipo1 = new TipoMetodoPagamento();
		tipo1.setNome("attivo1");
		tipo1.setAttivo(true);
		tipoMetodoPagamentoRepo.save(tipo1);

		TipoMetodoPagamento tipo2 = new TipoMetodoPagamento();
		tipo2.setNome("attivo2");
		tipo2.setAttivo(true);
		tipoMetodoPagamentoRepo.save(tipo2);

		List<TipoMetodoPagamentoDTO> lista = tipoMetodoPagamentoService.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(TipoMetodoPagamentoDTO::getAttivo));
	}
}
