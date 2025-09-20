package com.betacom.retrogames.controller;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.request.CategoriaReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.CategoriaService;

@Transactional
@SpringBootTest
public class CategoriaControllerTest {

	@Autowired
	private CategoriaController controller;

	private CategoriaReq createReq(String nome) {
		CategoriaReq req = new CategoriaReq();
		req.setNome(normalizza(nome));
		return req;
	}

	@Test
	void testCreateSuccesso() {
		ResponseBase res = controller.create(createReq("categoria_test"));
		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("creata"));
	}

	@Test
	void testCreateFallito() {
		controller.create(createReq("categoria_dup"));
		ResponseBase res = controller.create(createReq("categoria_dup"));
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("esistente"));
	}

	@Test
	void testUpdateSuccesso() {
		ResponseBase createRes = controller.create(createReq("categoria_update"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		CategoriaReq updateReq = createReq("categoria_updated");
		updateReq.setId(id);
		ResponseBase updateRes = controller.update(updateReq);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("aggiornata"));
	}

	@Test
	void testUpdateFallito() {
		CategoriaReq req = createReq("fake_categoria");
		req.setId(99999);
		ResponseBase res = controller.update(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testDisableSuccesso() {
		ResponseBase createRes = controller.create(createReq("categoria_disable"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		CategoriaReq disableReq = createReq("categoria_disable");
		disableReq.setId(id);
		ResponseBase disableRes = controller.disable(disableReq);
		assertTrue(disableRes.getReturnCode());
		assertNotNull(disableRes.getMsg());
		assertTrue(disableRes.getMsg().toLowerCase().contains("disattivata"));
	}

	@Test
	void testDisableFallito() {
		CategoriaReq req = createReq("fake_disable");
		req.setId(99999);
		ResponseBase res = controller.disable(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testGetByIdSuccesso() {
		ResponseBase createRes = controller.create(createReq("categoria_get"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ResponseObject<CategoriaDTO> res = controller.getById(id);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
		assertEquals("categoria_get", res.getDati().getNome());
	}

	@Test
	void testGetByIdFallito() {
		ResponseObject<CategoriaDTO> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testListActiveSuccesso() {
		ResponseBase res1 = controller.create(createReq("categoria_active1"));
		ResponseBase res2 = controller.create(createReq("categoria_active2"));

		assertTrue(res1.getReturnCode());
		assertTrue(res2.getReturnCode());

		Integer id1 = Integer.parseInt(res1.getMsg().replaceAll("\\D+", ""));
		Integer id2 = Integer.parseInt(res2.getMsg().replaceAll("\\D+", ""));

		ResponseList<CategoriaDTO> resList = controller.listActive();

		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());

		boolean containsId1 = false;
		boolean containsId2 = false;

		for (CategoriaDTO dto : resList.getDati()) {
			if (dto.getId().equals(id1)) {
				containsId1 = true;
			}
			if (dto.getId().equals(id2)) {
				containsId2 = true;
			}
		}

		assertTrue(containsId1, "La lista delle categorie attive dovrebbe contenere 'categoria_active1'");
		assertTrue(containsId2, "La lista delle categorie attive dovrebbe contenere 'categoria_active2'");
	}

	@Test
	void testListActiveFallito() {
		CategoriaController controllerWithError = new CategoriaController(new CategoriaService() {
			@Override
			public Integer crea(CategoriaReq req) {
				return null;
			}

			@Override
			public void aggiorna(CategoriaReq req) {
			}

			@Override
			public void disattiva(CategoriaReq req) {
			}

			@Override
			public CategoriaDTO getById(Integer id) {
				return null;
			}

			@Override
			public List<CategoriaDTO> listActive() {
				throw new RuntimeException("Errore simulato");
			}
		});

		ResponseList<CategoriaDTO> res = controllerWithError.listActive();
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("Errore simulato"));
	}
}
