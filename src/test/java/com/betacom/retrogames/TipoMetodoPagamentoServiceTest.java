package com.betacom.retrogames;

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

@SpringBootTest
@Transactional
class TipoMetodoPagamentoServiceTest {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private TipoMetodoPagamentoService tipoMetodoPagamentoS;

	@Autowired
	private TipoMetodoPagamentoRepository tipoRepo;

	@Test
	void testCreaSuccesso() throws AcademyException {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setNome("crypto");
		Integer id = tipoMetodoPagamentoS.crea(req);
		assertNotNull(id);
		TipoMetodoPagamentoDTO dto = tipoMetodoPagamentoS.getById(id);
		assertEquals("crypto", dto.getNome());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaGiaEsistente() throws AcademyException {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setNome("paypal");
		assertThrows(AcademyException.class, () -> tipoMetodoPagamentoS.crea(req));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		TipoMetodoPagamento tipo = tipoRepo.findByNome("carta").orElseThrow();
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO,
				new CachedTipoMetodoPagamento(tipo));
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setId(tipo.getId());
		req.setNome("CartaAggiornata");
		tipoMetodoPagamentoS.aggiorna(req);
		TipoMetodoPagamentoDTO updated = tipoMetodoPagamentoS.getById(tipo.getId());
		assertEquals("cartaaggiornata", updated.getNome());
	}

	@Test
	void testAggiornaNonEsistente() {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setId(999);
		req.setNome("fake");
		assertThrows(AcademyException.class, () -> tipoMetodoPagamentoS.aggiorna(req));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setId(1);
		tipoMetodoPagamentoS.disattiva(req);
		assertFalse(cacheManager.isRecordCached(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getId()));
	}

	@Test
	void testDisattivaNonEsistenteLanciaEccezione() {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setId(9999);
		assertThrows(AcademyException.class, () -> tipoMetodoPagamentoS.disattiva(req));
	}

	@Test
	void testListActive() {
		List<TipoMetodoPagamentoDTO> lista = tipoMetodoPagamentoS.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(TipoMetodoPagamentoDTO::getAttivo));
	}
}