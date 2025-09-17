package com.betacom.retrogames;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.controller.RuoloController;
import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.RuoloService;

@SpringBootTest
@Transactional
public class RuoloControllerTest {

	@Autowired
	private RuoloController controller;

	private RuoloReq createValidReq(String nome) {
		RuoloReq req = new RuoloReq();
		req.setNome(normalizza(nome));
		return req;
	}

	private RuoloReq createValidReqWithId(String nome, Integer id) {
		RuoloReq req = new RuoloReq();
		req.setNome(normalizza(nome));
		req.setId(id);
		return req;
	}

	@Test
	void testCreateSuccess() throws Exception {
		RuoloReq req = createValidReq("admin_test");
		ResponseBase res = controller.create(req);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("creato"));
	}

	@Test
	void testCreateDuplicateFails() throws Exception {
		RuoloReq req1 = createValidReq("admin_dup");
		RuoloReq req2 = createValidReq("admin_dup");

		ResponseBase res1 = controller.create(req1);
		assertTrue(res1.getReturnCode());

		ResponseBase res2 = controller.create(req2);
		assertFalse(res2.getReturnCode());
		assertNotNull(res2.getMsg());
		assertTrue(res2.getMsg().toLowerCase().contains("esistente"));
	}

	@Test
	void testUpdateSuccess() throws Exception {
		// Creo un ruolo
		ResponseBase createRes = controller.create(createValidReq("admin_update"));
		assertTrue(createRes.getReturnCode());
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		// Aggiorno
		RuoloReq updateReq = createValidReqWithId("admin_updated", id);
		ResponseBase updateRes = controller.update(updateReq);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("aggiornato"));

		// Controllo tramite getById
		ResponseObject<RuoloDTO> getRes = controller.getById(id);
		assertTrue(getRes.getReturnCode());
		assertEquals("admin_updated", getRes.getDati().getNome());
	}

	@Test
	void testUpdateNonExistentFails() {
		RuoloReq req = createValidReqWithId("fake_role", 99999);
		ResponseBase res = controller.update(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testDisableSuccess() throws Exception {
		ResponseBase createRes = controller.create(createValidReq("admin_disable"));
		assertTrue(createRes.getReturnCode());
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		RuoloReq disableReq = createValidReqWithId("admin_disable", id);
		ResponseBase disableRes = controller.disable(disableReq);
		assertTrue(disableRes.getReturnCode());
		assertNotNull(disableRes.getMsg());
		assertTrue(disableRes.getMsg().toLowerCase().contains("disattivato"));
	}

	@Test
	void testDisableNonExistentFails() {
		RuoloReq req = createValidReqWithId("fake_disable", 99999);
		ResponseBase res = controller.disable(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testGetByIdSuccess() throws Exception {
		ResponseBase createRes = controller.create(createValidReq("admin_get"));
		assertTrue(createRes.getReturnCode());
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ResponseObject<RuoloDTO> getRes = controller.getById(id);
		assertTrue(getRes.getReturnCode());
		assertNotNull(getRes.getDati());
		assertEquals("admin_get", getRes.getDati().getNome());
	}

	@Test
	void testGetByIdNonExistentFails() {
		ResponseObject<RuoloDTO> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovato"));
	}

	@Test
	void testListActive() {
		// Creo due ruoli
		controller.create(createValidReq("admin_active1"));
		controller.create(createValidReq("admin_active2"));

		ResponseList<RuoloDTO> resList = controller.listActive();
		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());
		assertTrue(resList.getDati().size() >= 2);

		boolean contains1 = resList.getDati().stream().anyMatch(dto -> dto.getNome().equalsIgnoreCase("admin_active1"));
		boolean contains2 = resList.getDati().stream().anyMatch(dto -> dto.getNome().equalsIgnoreCase("admin_active2"));

		assertTrue(contains1);
		assertTrue(contains2);
	}

	@Test
	void testListActiveException() {
		// Creo un controller temporaneo con servizio che lancia eccezione
		RuoloController controllerWithError = new RuoloController(new RuoloService() {
			@Override
			public Integer crea(RuoloReq req) {
				return null;
			}

			@Override
			public void aggiorna(RuoloReq req) throws AcademyException {
			}

			@Override
			public void disattiva(RuoloReq req) throws AcademyException {
			}

			@Override
			public RuoloDTO getById(Integer id) {
				return null;
			}

			@Override
			public java.util.List<RuoloDTO> listActive() {
				throw new RuntimeException("Errore simulato");
			}
		});

		ResponseList<RuoloDTO> res = controllerWithError.listActive();
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("Errore simulato"));
	}
}
