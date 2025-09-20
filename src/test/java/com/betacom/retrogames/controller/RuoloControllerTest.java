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

import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.RuoloService;

@Transactional
@SpringBootTest
public class RuoloControllerTest {

	@Autowired
	private RuoloController controller;

	private RuoloReq createReq(String nome) {
		RuoloReq req = new RuoloReq();
		req.setNome(normalizza(nome));
		return req;
	}

	@Test
	void testCreateSuccesso() {
		ResponseBase res = controller.create(createReq("admin_test"));
		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("creato"));
	}

	@Test
	void testCreateFallito() {
		controller.create(createReq("admin_dup"));
		ResponseBase res = controller.create(createReq("admin_dup"));
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("esistente"));
	}

	@Test
	void testUpdateSuccesso() {
		ResponseBase createRes = controller.create(createReq("admin_update"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		RuoloReq updateReq = createReq("admin_updated");
		updateReq.setId(id);
		ResponseBase updateRes = controller.update(updateReq);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("aggiornato"));
	}

	@Test
	void testUpdateFallito() {
		RuoloReq req = createReq("fake_role");
		req.setId(99999);
		ResponseBase res = controller.update(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testDisableSuccesso() {
		ResponseBase createRes = controller.create(createReq("admin_disable"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		RuoloReq disableReq = createReq("admin_disable");
		disableReq.setId(id);
		ResponseBase disableRes = controller.disable(disableReq);
		assertTrue(disableRes.getReturnCode());
		assertNotNull(disableRes.getMsg());
		assertTrue(disableRes.getMsg().toLowerCase().contains("disattivato"));
	}

	@Test
	void testDisableFallito() {
		RuoloReq req = createReq("fake_disable");
		req.setId(99999);
		ResponseBase res = controller.disable(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testGetByIdSuccesso() {
		ResponseBase createRes = controller.create(createReq("admin_get"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ResponseObject<RuoloDTO> res = controller.getById(id);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
		assertEquals("admin_get", res.getDati().getNome());
	}

	@Test
	void testGetByIdFallito() {
		ResponseObject<RuoloDTO> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testListActiveSuccesso() {
		ResponseBase res1 = controller.create(createReq("admin_active1"));
		ResponseBase res2 = controller.create(createReq("admin_active2"));

		assertTrue(res1.getReturnCode());
		assertTrue(res2.getReturnCode());

		Integer id1 = Integer.parseInt(res1.getMsg().replaceAll("\\D+", ""));
		Integer id2 = Integer.parseInt(res2.getMsg().replaceAll("\\D+", ""));

		assertNotNull(id1);
		assertNotNull(id2);

		ResponseList<RuoloDTO> resList = controller.listActive();

		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());

		boolean containsId1 = false;
		boolean containsId2 = false;

		for (RuoloDTO dto : resList.getDati()) {
			if (dto.getId().equals(id1)) {
				containsId1 = true;
			}
			if (dto.getId().equals(id2)) {
				containsId2 = true;
			}
		}

		assertTrue(containsId1, "La lista dei ruoli attivi dovrebbe contenere 'admin_active1'");
		assertTrue(containsId2, "La lista dei ruoli attivi dovrebbe contenere 'admin_active2'");
	}

	@Test
	void testListActiveFallito() {
		RuoloController controllerWithError = new RuoloController(new RuoloService() {
			@Override
			public Integer crea(RuoloReq req) {
				return null;
			}

			@Override
			public void aggiorna(RuoloReq req) {
			}

			@Override
			public void disattiva(RuoloReq req) {
			}

			@Override
			public RuoloDTO getById(Integer id) {
				return null;
			}

			@Override
			public List<RuoloDTO> listActive() {
				throw new RuntimeException("Errore simulato");
			}
		});

		ResponseList<RuoloDTO> res = controllerWithError.listActive();
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("Errore simulato"));
	}
}
