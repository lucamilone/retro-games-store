package com.betacom.retrogames.controller;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.request.ProdottoReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.ProdottoService;

@Transactional
@SpringBootTest
public class ProdottoControllerTest {

	@Autowired
	private ProdottoController controller;

	private ProdottoReq createReq(Integer categoriaId, String sku) {
		ProdottoReq req = new ProdottoReq();
		req.setNome("Prodotto Test");
		req.setDescrizione("Descrizione Test");
		req.setSku(normalizza(sku));
		req.setAnnoUscita(2001);
		req.setPrezzo(new BigDecimal("59.99"));
		req.setCategoriaId(categoriaId);
		return req;
	}

	@Test
	void testCreateSuccesso() {
		Integer categoriaId = 1;
		ResponseBase res = controller.create(createReq(categoriaId, "SKU1"));
		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("creato"));
	}

	@Test
	void testCreateFallito() {
		Integer categoriaId = 1;
		controller.create(createReq(categoriaId, "SKU_DUP"));
		ResponseBase res = controller.create(createReq(categoriaId, "SKU_DUP"));
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("esistente"));
	}

	@Test
	void testUpdateSuccesso() {
		Integer categoriaId = 1;
		ResponseBase createRes = controller.create(createReq(categoriaId, "SKU_UPD"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ProdottoReq updateReq = createReq(categoriaId, "SKU_UPD2");
		updateReq.setId(id);
		ResponseBase updateRes = controller.update(updateReq);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("aggiornato"));
	}

	@Test
	void testUpdateFallito() {
		ProdottoReq req = createReq(1, "FAKE_UPD");
		req.setId(99999);
		ResponseBase res = controller.update(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testDisableSuccesso() {
		Integer categoriaId = 1;
		ResponseBase createRes = controller.create(createReq(categoriaId, "SKU_DIS"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ProdottoReq disableReq = createReq(categoriaId, "SKU_DIS");
		disableReq.setId(id);
		ResponseBase disableRes = controller.disable(disableReq);
		assertTrue(disableRes.getReturnCode());
		assertNotNull(disableRes.getMsg());
		assertTrue(disableRes.getMsg().toLowerCase().contains("disattivato"));
	}

	@Test
	void testDisableFallito() {
		ProdottoReq req = createReq(1, "FAKE_DIS");
		req.setId(99999);
		ResponseBase res = controller.disable(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testGetByIdSuccesso() {
		Integer categoriaId = 1;
		ResponseBase createRes = controller.create(createReq(categoriaId, "SKU_GET"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ResponseObject<ProdottoDTO> res = controller.getById(id);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
		assertEquals("Prodotto Test", res.getDati().getNome());
	}

	@Test
	void testGetByIdFallito() {
		ResponseObject<ProdottoDTO> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testListActiveSuccesso() {
		Integer categoriaId = 1;
		ResponseBase res1 = controller.create(createReq(categoriaId, "SKU_ACTIVE1"));
		ResponseBase res2 = controller.create(createReq(categoriaId, "SKU_ACTIVE2"));

		assertTrue(res1.getReturnCode());
		assertTrue(res2.getReturnCode());

		Integer id1 = Integer.parseInt(res1.getMsg().replaceAll("\\D+", ""));
		Integer id2 = Integer.parseInt(res2.getMsg().replaceAll("\\D+", ""));

		ResponseList<ProdottoDTO> resList = controller.listActive();

		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());

		boolean containsId1 = false;
		boolean containsId2 = false;

		for (ProdottoDTO dto : resList.getDati()) {
			if (dto.getId().equals(id1)) {
				containsId1 = true;
			}
			if (dto.getId().equals(id2)) {
				containsId2 = true;
			}
		}

		assertTrue(containsId1, "La lista dei prodotti attivi dovrebbe contenere SKU_ACTIVE1");
		assertTrue(containsId2, "La lista dei prodotti attivi dovrebbe contenere SKU_ACTIVE2");
	}

	@Test
	void testListActiveFallito() {
		ProdottoController controllerWithError = new ProdottoController(new ProdottoService() {
			@Override
			public Integer crea(ProdottoReq req) {
				return null;
			}

			@Override
			public void aggiorna(ProdottoReq req) {
			}

			@Override
			public void disattiva(ProdottoReq req) {
			}

			@Override
			public ProdottoDTO getById(Integer id) {
				return null;
			}

			@Override
			public List<ProdottoDTO> listActive() {
				throw new RuntimeException("Errore simulato");
			}
		});

		ResponseList<ProdottoDTO> res = controllerWithError.listActive();
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("Errore simulato"));
	}
}
