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

import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;
import com.betacom.retrogames.service.interfaces.TipoMetodoPagamentoService;

@SpringBootTest
@Transactional
class TipoMetodoPagamentoServiceTest {

	@Autowired
	private TipoMetodoPagamentoService tipoMetodoPagamentoService;

	private TipoMetodoPagamentoReq createReq(String nome) {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setNome(nome);
		return req;
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		TipoMetodoPagamentoReq req = createReq("crypto");
		Integer id = tipoMetodoPagamentoService.crea(req);
		assertNotNull(id);

		TipoMetodoPagamentoDTO dto = tipoMetodoPagamentoService.getById(id);
		assertEquals("crypto", dto.getNome());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaGiaEsistente() throws AcademyException {
		TipoMetodoPagamentoReq req = createReq("paypal");
		assertThrows(AcademyException.class, () -> tipoMetodoPagamentoService.crea(req));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		// Creiamo un nuovo tipo e prendiamo l'ID restituito
		TipoMetodoPagamentoReq reqCrea = createReq("aggiorna_test");
		Integer id = tipoMetodoPagamentoService.crea(reqCrea);

		// Aggiorniamo il tipo creato
		TipoMetodoPagamentoReq reqUpdate = createReq("aggiornato_test");
		reqUpdate.setId(id);
		tipoMetodoPagamentoService.aggiorna(reqUpdate);

		TipoMetodoPagamentoDTO updated = tipoMetodoPagamentoService.getById(id);
		assertEquals("aggiornato_test", updated.getNome());
	}

	@Test
	void testAggiornaNonEsistente() {
		TipoMetodoPagamentoReq req = createReq("fake");
		req.setId(9999);
		assertThrows(AcademyException.class, () -> tipoMetodoPagamentoService.aggiorna(req));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		TipoMetodoPagamentoReq reqCrea = createReq("disattiva_test");
		Integer id = tipoMetodoPagamentoService.crea(reqCrea);

		TipoMetodoPagamentoReq reqDisattiva = new TipoMetodoPagamentoReq();
		reqDisattiva.setId(id);
		tipoMetodoPagamentoService.disattiva(reqDisattiva);

		TipoMetodoPagamentoDTO dto = tipoMetodoPagamentoService.getById(id);
		assertFalse(dto.getAttivo());
	}

	@Test
	void testDisattivaNonEsistenteLanciaEccezione() {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setId(9999);
		assertThrows(AcademyException.class, () -> tipoMetodoPagamentoService.disattiva(req));
	}

	@Test
	void testListActive() throws AcademyException {
		// Creiamo due tipi attivi
		Integer id1 = tipoMetodoPagamentoService.crea(createReq("attivo1"));
		Integer id2 = tipoMetodoPagamentoService.crea(createReq("attivo2"));

		List<TipoMetodoPagamentoDTO> lista = tipoMetodoPagamentoService.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(TipoMetodoPagamentoDTO::getAttivo));

		boolean contains1 = lista.stream().anyMatch(dto -> dto.getId().equals(id1));
		boolean contains2 = lista.stream().anyMatch(dto -> dto.getId().equals(id2));
		assertTrue(contains1);
		assertTrue(contains2);
	}
}
