package com.betacom.retrogames;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.controller.TipoMetodoPagamentoController;
import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.TipoMetodoPagamentoService;

@SpringBootTest
@Transactional
class TipoMetodoPagamentoControllerTest {

	@Autowired
	private TipoMetodoPagamentoController controller;

	private TipoMetodoPagamentoReq createValidReq(String nome) {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setNome(normalizza(nome));
		return req;
	}

	private TipoMetodoPagamentoReq createValidReqWithId(String nome, Integer id) {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setNome(normalizza(nome));
		req.setId(id);
		return req;
	}

	@Test
	void testCreateSuccess() {
		TipoMetodoPagamentoReq req = createValidReq("Pagamento OK");
		ResponseBase res = controller.create(req);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("creato"));
	}

	@Test
	void testCreateDuplicateNameFails() {
		TipoMetodoPagamentoReq req1 = createValidReq("Pagamento Duplicato");
		TipoMetodoPagamentoReq req2 = createValidReq("Pagamento Duplicato");

		ResponseBase res1 = controller.create(req1);
		assertTrue(res1.getReturnCode());

		ResponseBase res2 = controller.create(req2);
		assertFalse(res2.getReturnCode());
		assertNotNull(res2.getMsg());
		assertTrue(res2.getMsg().contains("esistente"));
	}

	@Test
	void testUpdateSuccess() {
		// Creo un tipo
		ResponseBase createRes = controller.create(createValidReq("Pagamento Aggiorna"));
		assertTrue(createRes.getReturnCode());
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		// Aggiorno usando l'ID estratto
		TipoMetodoPagamentoReq reqUpdate = createValidReqWithId("Pagamento Aggiornato", id);
		ResponseBase updateRes = controller.update(reqUpdate);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("aggiornato"));
	}

	@Test
	void testUpdateNonExistentIdFails() {
		TipoMetodoPagamentoReq req = createValidReqWithId("NonEsistente", 99999);
		ResponseBase res = controller.update(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("non-trovato"));
	}

	@Test
	void testDisableSuccess() {
		TipoMetodoPagamentoReq reqCreate = createValidReq("Pagamento Disabilita");
		ResponseBase createRes = controller.create(reqCreate);
		assertTrue(createRes.getReturnCode());

		TipoMetodoPagamentoReq reqDisable = createValidReqWithId("Pagamento Disabilita", 1);
		ResponseBase disableRes = controller.disable(reqDisable);
		assertTrue(disableRes.getReturnCode());
		assertNotNull(disableRes.getMsg());
		assertTrue(disableRes.getMsg().contains("disattivato"));
	}

	@Test
	void testDisableNonExistentIdFails() {
		TipoMetodoPagamentoReq req = createValidReqWithId("NonEsistente", 99999);
		ResponseBase res = controller.disable(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("non-trovato"));
	}

	@Test
	void testGetByIdSuccess() {
		ResponseBase createRes = controller.create(createValidReq("Pagamento Get"));
		assertTrue(createRes.getReturnCode());
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ResponseObject<?> res = controller.getById(id);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
	}

	@Test
	void testGetByIdNonExistentFails() {
		ResponseObject<?> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("non-trovato"));
	}

	@Test
	void testListActive() {
		// Creo due tipi
		TipoMetodoPagamentoReq req1 = createValidReq("Pagamento Attivo 1");
		TipoMetodoPagamentoReq req2 = createValidReq("Pagamento Attivo 2");

		ResponseBase res1 = controller.create(req1);
		ResponseBase res2 = controller.create(req2);

		assertTrue(res1.getReturnCode());
		assertTrue(res2.getReturnCode());

		// Recupero gli ID dai messaggi
		Integer id1 = Integer.parseInt(res1.getMsg().replaceAll("\\D+", ""));
		Integer id2 = Integer.parseInt(res2.getMsg().replaceAll("\\D+", ""));

		assertNotNull(id1);
		assertNotNull(id2);

		// Ora chiamo listActive
		var resList = controller.listActive();
		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());
		assertTrue(resList.getDati().size() >= 2);

		// Controllo che i tipi appena creati siano presenti
		boolean contains1 = resList.getDati().stream().anyMatch(dto -> dto.getId().equals(id1));
		boolean contains2 = resList.getDati().stream().anyMatch(dto -> dto.getId().equals(id2));

		assertTrue(contains1, "La lista dei tipi attivi dovrebbe contenere 'Pagamento Attivo 1'");
		assertTrue(contains2, "La lista dei tipi attivi dovrebbe contenere 'Pagamento Attivo 2'");
	}

	@Test
	void testListActiveException() {
		// Creo un controller temporaneo con service che lancia eccezione
		TipoMetodoPagamentoController controllerWithError = new TipoMetodoPagamentoController(
				new TipoMetodoPagamentoService() {
					@Override
					public List<TipoMetodoPagamentoDTO> listActive() {
						throw new RuntimeException("Errore simulato");
					}

					@Override
					public Integer crea(TipoMetodoPagamentoReq req) {
						return null;
					}

					@Override
					public void aggiorna(TipoMetodoPagamentoReq req) {
					}

					@Override
					public void disattiva(TipoMetodoPagamentoReq req) {
					}

					@Override
					public TipoMetodoPagamentoDTO getById(Integer id) {
						return null;
					}
				});

		var res = controllerWithError.listActive();

		assertFalse(res.getReturnCode()); // ritorna false come atteso
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("Errore simulato"));
	}
}