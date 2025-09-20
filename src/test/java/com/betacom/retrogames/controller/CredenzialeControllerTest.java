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

import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.CredenzialeService;

@Transactional
@SpringBootTest
public class CredenzialeControllerTest {

	@Autowired
	private CredenzialeController controller;

	private CredenzialeReq createReq(String email, String password) {
		CredenzialeReq req = new CredenzialeReq();
		req.setEmail(normalizza(email));
		req.setPassword(password);
		return req;
	}

	@Test
	void testCreateSuccesso() {
		ResponseBase res = controller.create(createReq("user_test@mail.com", "pass123"));
		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("creata"));
	}

	@Test
	void testCreateFallito() {
		controller.create(createReq("user_dup@mail.com", "pass123"));
		ResponseBase res = controller.create(createReq("user_dup@mail.com", "pass123"));
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("esistente"));
	}

	@Test
	void testUpdateEmailSuccesso() {
		ResponseBase createRes = controller.create(createReq("user_update@mail.com", "pass123"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		CredenzialeReq updateReq = createReq("user_updated@mail.com", "pass123");
		updateReq.setId(id);
		ResponseBase updateRes = controller.updateEmail(updateReq);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("email aggiornata"));
	}

	@Test
	void testUpdateEmailFallito() {
		CredenzialeReq req = createReq("fake@mail.com", "pass123");
		req.setId(99999);
		ResponseBase res = controller.updateEmail(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testUpdatePasswordSuccesso() {
		ResponseBase createRes = controller.create(createReq("user_pass@mail.com", "oldpass"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		CredenzialeReq updateReq = createReq("user_pass@mail.com", "newpass");
		updateReq.setId(id);
		ResponseBase updateRes = controller.updatePassword(updateReq);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("password aggiornata"));
	}

	@Test
	void testUpdatePasswordFallito() {
		CredenzialeReq req = createReq("fake@mail.com", "pass123");
		req.setId(99999);
		ResponseBase res = controller.updatePassword(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testLoginSuccesso() {
		controller.create(createReq("user_login@mail.com", "pass123"));

		CredenzialeReq loginReq = createReq("user_login@mail.com", "pass123");
		ResponseObject<CredenzialeDTO> res = controller.login(loginReq);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
		assertEquals("user_login@mail.com", res.getDati().getEmail());
	}

	@Test
	void testLoginFallito() {
		CredenzialeReq loginReq = createReq("fake_login@mail.com", "wrongpass");
		ResponseObject<CredenzialeDTO> res = controller.login(loginReq);

		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("credenziali-non-valide"));
	}

	@Test
	void testGetByIdSuccesso() {
		ResponseBase createRes = controller.create(createReq("user_get@mail.com", "pass123"));
		Integer id = Integer.parseInt(createRes.getMsg().replaceAll("\\D+", ""));

		ResponseObject<CredenzialeDTO> res = controller.getById(id);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
		assertEquals("user_get@mail.com", res.getDati().getEmail());
	}

	@Test
	void testGetByIdFallito() {
		ResponseObject<CredenzialeDTO> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testListActiveSuccesso() {
		ResponseBase res1 = controller.create(createReq("active1@mail.com", "pass1"));
		ResponseBase res2 = controller.create(createReq("active2@mail.com", "pass2"));

		Integer id1 = Integer.parseInt(res1.getMsg().replaceAll("\\D+", ""));
		Integer id2 = Integer.parseInt(res2.getMsg().replaceAll("\\D+", ""));

		ResponseList<CredenzialeDTO> resList = controller.listActive();
		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());

		boolean containsId1 = false;
		boolean containsId2 = false;

		for (CredenzialeDTO dto : resList.getDati()) {
			if (dto.getId().equals(id1)) {
				containsId1 = true;
			}
			if (dto.getId().equals(id2)) {
				containsId2 = true;
			}
		}

		assertTrue(containsId1, "La lista delle credenziali attive dovrebbe contenere 'active1@mail.com'");
		assertTrue(containsId2, "La lista delle credenziali attive dovrebbe contenere 'active2@mail.com'");
	}

	@Test
	void testListActiveFallito() {
		CredenzialeController controllerWithError = new CredenzialeController(new CredenzialeService() {
			@Override
			public Integer crea(CredenzialeReq req) {
				return null;
			}

			@Override
			public void disattiva(CredenzialeReq req) throws AcademyException {
				// TODO Auto-generated method stub

			}

			@Override
			public void riattiva(CredenzialeReq req) throws AcademyException {
				// TODO Auto-generated method stub

			}

			@Override
			public void aggiornaEmail(CredenzialeReq req) {
			}

			@Override
			public void aggiornaPassword(CredenzialeReq req) {
			}

			@Override
			public CredenzialeDTO signIn(CredenzialeReq req) {
				return null;
			}

			@Override
			public CredenzialeDTO getById(Integer id) {
				return null;
			}

			@Override
			public List<CredenzialeDTO> listActive() {
				throw new RuntimeException("Errore simulato");
			}
		});

		ResponseList<CredenzialeDTO> res = controllerWithError.listActive();
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("Errore simulato"));
	}
}
