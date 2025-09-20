package com.betacom.retrogames.controller;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.TipoMetodoPagamentoService;

@Transactional
@SpringBootTest
public class TipoMetodoPagamentoControllerTest {

	@Autowired
	private TipoMetodoPagamentoController controller;

	private TipoMetodoPagamentoReq createReq(String nome) {
		TipoMetodoPagamentoReq req = new TipoMetodoPagamentoReq();
		req.setNome(normalizza(nome));
		return req;
	}

	@Test
	void testCreateSuccesso() {
		TipoMetodoPagamentoReq req = createReq("Pagamento OK");
		ResponseBase res = controller.create(req);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("creato"));
	}

	@Test
	void testCreateFallito() {
		TipoMetodoPagamentoReq req1 = createReq("Pagamento Duplicato");
		TipoMetodoPagamentoReq req2 = createReq("Pagamento Duplicato");

		ResponseBase res1 = controller.create(req1);
		assertTrue(res1.getReturnCode());

		ResponseBase res2 = controller.create(req2);
		assertFalse(res2.getReturnCode());
		assertNotNull(res2.getMsg());
		assertTrue(res2.getMsg().toLowerCase().contains("esistente"));
	}

	@Test
	void testUpdateSuccesso() {
		ResponseBase createRes = controller.create(createReq("Pagamento Aggiorna"));
		assertTrue(createRes.getReturnCode());
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		TipoMetodoPagamentoReq reqUpdate = createReq("Pagamento Aggiornato");
		reqUpdate.setId(id);

		ResponseBase updateRes = controller.update(reqUpdate);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("aggiornato"));
	}

	@Test
	void testUpdateFallito() {
		TipoMetodoPagamentoReq req = createReq("NonEsistente");
		req.setId(99999);
		ResponseBase res = controller.update(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testDisableSuccesso() {
		TipoMetodoPagamentoReq reqCreate = createReq("Pagamento Disabilita");
		ResponseBase createRes = controller.create(reqCreate);
		assertTrue(createRes.getReturnCode());

		Integer idDisable = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));
		TipoMetodoPagamentoReq reqDisable = createReq("Pagamento Disabilita");
		reqDisable.setId(idDisable);

		ResponseBase disableRes = controller.disable(reqDisable);
		assertTrue(disableRes.getReturnCode());
		assertNotNull(disableRes.getMsg());
		assertTrue(disableRes.getMsg().toLowerCase().contains("disattivato"));
	}

	@Test
	void testDisableFallito() {
		TipoMetodoPagamentoReq req = createReq("NonEsistente");
		req.setId(99999);
		ResponseBase res = controller.disable(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testGetByIdSuccesso() {
		ResponseBase createRes = controller.create(createReq("Pagamento Get"));
		assertTrue(createRes.getReturnCode());
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ResponseObject<TipoMetodoPagamentoDTO> res = controller.getById(id);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
	}

	@Test
	void testGetByIdFallito() {
		ResponseObject<TipoMetodoPagamentoDTO> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testListActiveSuccesso() {
		ResponseBase res1 = controller.create(createReq("Pagamento Attivo 1"));
		ResponseBase res2 = controller.create(createReq("Pagamento Attivo 2"));

		assertTrue(res1.getReturnCode());
		assertTrue(res2.getReturnCode());

		Integer id1 = Integer.parseInt(res1.getMsg().replaceAll("\\D+", ""));
		Integer id2 = Integer.parseInt(res2.getMsg().replaceAll("\\D+", ""));

		assertNotNull(id1);
		assertNotNull(id2);

		ResponseList<TipoMetodoPagamentoDTO> resList = controller.listActive();
		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());

		boolean containsId1 = false;
		boolean containsId2 = false;
		for (TipoMetodoPagamentoDTO dto : resList.getDati()) {
			if (dto.getId().equals(id1)) {
				containsId1 = true;
			}
			if (dto.getId().equals(id2)) {
				containsId2 = true;
			}
		}

		assertTrue(containsId1, "La lista dei tipi attivi dovrebbe contenere 'Pagamento Attivo 1'");
		assertTrue(containsId2, "La lista dei tipi attivi dovrebbe contenere 'Pagamento Attivo 2'");
	}

	@Test
	void testListActiveFallito() {
		TipoMetodoPagamentoController controllerWithError = new TipoMetodoPagamentoController(
				new TipoMetodoPagamentoService() {
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

					@Override
					public List<TipoMetodoPagamentoDTO> listActive() {
						throw new RuntimeException("Errore simulato");
					}
				});

		ResponseList<TipoMetodoPagamentoDTO> res = controllerWithError.listActive();
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("errore simulato"));
	}
}
