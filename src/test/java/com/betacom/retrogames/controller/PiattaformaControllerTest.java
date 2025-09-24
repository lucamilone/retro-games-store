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

import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.PiattaformaService;

@Transactional
@SpringBootTest
public class PiattaformaControllerTest {

	@Autowired
	private PiattaformaController controller;

	private PiattaformaReq createReq(String nome, String codice, Integer annoUscita) {
		PiattaformaReq req = new PiattaformaReq();
		req.setNome(nome);
		req.setCodice(normalizza(codice));
		req.setAnnoUscitaPiattaforma(annoUscita);
		req.setAttivo(true);
		return req;
	}

	@Test
	void testCreateSuccesso() {
		ResponseObject<PiattaformaDTO> res = controller.create(createReq("Mega Drive", "md", 1988));
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
		assertTrue(res.getMsg().toLowerCase().contains("creata"));
	}

	@Test
	void testCreateFallito() {
		controller.create(createReq("Super Nintendo", "sn", 1990));
		ResponseObject<PiattaformaDTO> res = controller.create(createReq("Super Nintendo", "sn", 1990));
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("esistente"));
	}

	@Test
	void testUpdateSuccesso() {
		ResponseObject<PiattaformaDTO> createRes = controller.create(createReq("Atari", "at", 1980));
		Integer id = createRes.getDati().getId();

		PiattaformaReq updateReq = createReq("Atari Updated", "au", 1981);
		updateReq.setId(id);
		ResponseBase updateRes = controller.update(updateReq);
		assertTrue(updateRes.getReturnCode());
		assertNotNull(updateRes.getMsg());
		assertTrue(updateRes.getMsg().toLowerCase().contains("aggiornata"));
	}

	@Test
	void testUpdateFallito() {
		PiattaformaReq req = createReq("Fake Platform", "fp", 2000);
		req.setId(99999);
		ResponseBase res = controller.update(req);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testDisableSuccesso() {
		ResponseObject<PiattaformaDTO> createRes = controller.create(createReq("Atari Disable", "ad", 1980));
		Integer id = createRes.getDati().getId();
		PiattaformaReq disableReq = createReq("Atari Disable", "ad", 1980);
		disableReq.setId(id);
		ResponseBase disableRes = controller.disable(disableReq);
		assertTrue(disableRes.getReturnCode());
		assertNotNull(disableRes.getMsg());
		assertTrue(disableRes.getMsg().toLowerCase().contains("disattivata"));
	}

	@Test
	void testDisableFallito() {
		PiattaformaReq req = createReq("Fake Disable", "fd", 2000);
		req.setId(99999);
		ResponseBase res = controller.disable(req);

		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testGetByIdSuccesso() {
		ResponseObject<PiattaformaDTO> createRes = controller.create(createReq("Atari Get", "ag", 1980));
		Integer id = createRes.getDati().getId();

		ResponseObject<PiattaformaDTO> res = controller.getById(id);
		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
		assertEquals("Atari Get", res.getDati().getNome());
	}

	@Test
	void testGetByIdFallito() {
		ResponseObject<PiattaformaDTO> res = controller.getById(99999);
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
	}

	@Test
	void testListActiveSuccesso() {
		ResponseObject<PiattaformaDTO> res1 = controller.create(createReq("Atari Active1", "aa1", 1980));
		ResponseObject<PiattaformaDTO> res2 = controller.create(createReq("Atari Active2", "aa2", 1981));

		assertTrue(res1.getReturnCode());
		assertTrue(res2.getReturnCode());

		Integer id1 = res1.getDati().getId();
		Integer id2 = res2.getDati().getId();

		ResponseList<PiattaformaDTO> resList = controller.listActive();

		assertTrue(resList.getReturnCode());
		assertNotNull(resList.getDati());

		boolean containsId1 = false;
		boolean containsId2 = false;

		for (PiattaformaDTO dto : resList.getDati()) {
			if (dto.getId().equals(id1)) {
				containsId1 = true;
			}
			if (dto.getId().equals(id2)) {
				containsId2 = true;
			}
		}

		assertTrue(containsId1, "La lista delle piattaforme attive dovrebbe contenere 'Atari Active1'");
		assertTrue(containsId2, "La lista delle piattaforme attive dovrebbe contenere 'Atari Active2'");
	}

	@Test
	void testListActiveFallito() {
		PiattaformaController controllerWithError = new PiattaformaController(new PiattaformaService() {
			@Override
			public PiattaformaDTO crea(PiattaformaReq req) {
				return null;
			}

			@Override
			public void aggiorna(PiattaformaReq req) {
			}

			@Override
			public void disattiva(PiattaformaReq req) {
			}

			@Override
			public PiattaformaDTO getById(Integer id) {
				return null;
			}

			@Override
			public List<PiattaformaDTO> listActive() {
				throw new RuntimeException("Errore simulato");
			}
		});

		ResponseList<PiattaformaDTO> res = controllerWithError.listActive();
		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().contains("Errore simulato"));
	}
}
