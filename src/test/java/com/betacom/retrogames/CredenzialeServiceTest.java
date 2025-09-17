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

import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.service.interfaces.CredenzialeService;

@SpringBootTest
@Transactional
class CredenzialeServiceTest {
	@Autowired
	private CredenzialeService credenzialeService;

	@Test
	void testCreaSuccess() throws AcademyException {
		CredenzialeReq req = new CredenzialeReq();
		req.setEmail("test@email.com");
		req.setPassword("test123");

		Integer id = credenzialeService.crea(req);
		assertNotNull(id);

		CredenzialeDTO dto = credenzialeService.getById(id);
		assertEquals("test@email.com", dto.getEmail());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaEmailGiaEsistente() throws AcademyException {
		// Prima creazione
		CredenzialeReq req = new CredenzialeReq();
		req.setEmail("duplicate@email.com");
		req.setPassword("pass");

		credenzialeService.crea(req);

		// Seconda creazione fallisce
		assertThrows(AcademyException.class, () -> credenzialeService.crea(req));
	}

	@Test
	void testListActive() throws AcademyException {
		// Creo e attivo una credenziale
		CredenzialeReq req = new CredenzialeReq();
		req.setEmail("attivo@test.com");
		req.setPassword("pass");
		credenzialeService.crea(req);

		List<CredenzialeDTO> list = credenzialeService.listActive();
		assertFalse(list.isEmpty());
		assertTrue(list.stream().allMatch(CredenzialeDTO::getAttivo));
	}
}