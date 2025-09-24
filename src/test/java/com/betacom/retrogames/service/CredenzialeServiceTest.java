package com.betacom.retrogames.service;

import static com.betacom.retrogames.util.Utils.normalizza;
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
import com.betacom.retrogames.model.Credenziale;
import com.betacom.retrogames.repository.CredenzialeRepository;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.service.interfaces.CredenzialeService;

@Transactional
@SpringBootTest
public class CredenzialeServiceTest {

	@Autowired
	private CredenzialeService credenzialeService;

	@Autowired
	private CredenzialeRepository credenzialeRepo;

	private CredenzialeReq createReq(String email, String password) {
		CredenzialeReq req = new CredenzialeReq();
		req.setEmail(normalizza(email));
		req.setPassword(password);
		req.setAttivo(true);
		return req;
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		String email = "test@example.com";
		credenzialeRepo.findByEmail(normalizza(email)).ifPresent(credenzialeRepo::delete);

		CredenzialeReq req = createReq(email, "pass123");
		CredenzialeDTO dto = credenzialeService.crea(req);

		assertNotNull(dto);
		assertNotNull(dto.getId());
		assertEquals(normalizza(email), dto.getEmail());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaFallito() throws AcademyException {
		String email = "dup@example.com";
		credenzialeRepo.findByEmail(normalizza(email)).ifPresent(credenzialeRepo::delete);

		CredenzialeReq req1 = createReq(email, "pass123");
		credenzialeService.crea(req1);

		CredenzialeReq req2 = createReq(email, "pass123");
		AcademyException exception = assertThrows(AcademyException.class, () -> credenzialeService.crea(req2));
		assertTrue(exception.getMessage().contains("email-esistente"));
	}

	@Test
	void testAggiornaEmailSuccesso() throws AcademyException {
		String emailOriginale = "update@example.com";
		credenzialeRepo.findByEmail(normalizza(emailOriginale)).ifPresent(credenzialeRepo::delete);
		String emailEsistente = "exist@example.com";
		credenzialeRepo.findByEmail(normalizza(emailEsistente)).ifPresent(credenzialeRepo::delete);

		CredenzialeDTO dtoCreato = credenzialeService.crea(createReq(emailOriginale, "pass123"));
		Integer id = dtoCreato.getId();

		Credenziale c2 = new Credenziale();
		c2.setEmail(normalizza(emailEsistente));
		c2.setPassword("pass456");
		c2.setAttivo(true);
		credenzialeRepo.saveAndFlush(c2);

		CredenzialeReq reqUpdate = new CredenzialeReq();
		reqUpdate.setId(id);
		reqUpdate.setEmail("nuova@example.com");
		credenzialeService.aggiornaEmail(reqUpdate);

		CredenzialeDTO dto = credenzialeService.getById(id);
		assertEquals(normalizza("nuova@example.com"), dto.getEmail());

		reqUpdate.setEmail(normalizza(emailEsistente));
		AcademyException exception = assertThrows(AcademyException.class,
				() -> credenzialeService.aggiornaEmail(reqUpdate));
		assertTrue(exception.getMessage().contains("email-esistente"));
	}

	@Test
	void testAggiornaEmailFallito() {
		CredenzialeReq req = new CredenzialeReq();
		req.setId(9999);
		req.setEmail("nonexist@example.com");
		AcademyException exception = assertThrows(AcademyException.class, () -> credenzialeService.aggiornaEmail(req));
		assertTrue(exception.getMessage().contains("credenziale-non-trovata"));
	}

	@Test
	void testAggiornaPasswordSuccesso() throws AcademyException {
		String email = "passupdate@example.com";
		credenzialeRepo.findByEmail(normalizza(email)).ifPresent(credenzialeRepo::delete);

		CredenzialeDTO dtoCreato = credenzialeService.crea(createReq(email, "oldpass"));
		Integer id = dtoCreato.getId();

		CredenzialeReq reqUpdate = new CredenzialeReq();
		reqUpdate.setId(id);
		reqUpdate.setPassword("newpass");
		credenzialeService.aggiornaPassword(reqUpdate);

		Credenziale updated = credenzialeRepo.findById(id).orElseThrow();
		assertEquals("newpass", updated.getPassword());
	}

	@Test
	void testAggiornaPasswordFallito() {
		Integer missingId = 9999;
		credenzialeRepo.findById(missingId).ifPresent(credenzialeRepo::delete);

		CredenzialeReq req = new CredenzialeReq();
		req.setId(missingId);
		req.setPassword("newPassword");

		AcademyException exception = assertThrows(AcademyException.class,
				() -> credenzialeService.aggiornaPassword(req));
		assertTrue(exception.getMessage().contains("credenziale-non-trovata"));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		String email = "disable@example.com";
		credenzialeRepo.findByEmail(normalizza(email)).ifPresent(credenzialeRepo::delete);

		CredenzialeDTO dtoCreato = credenzialeService.crea(createReq(email, "pass123"));
		Integer id = dtoCreato.getId();

		CredenzialeReq req = new CredenzialeReq();
		req.setId(id);
		credenzialeService.disattiva(req);

		CredenzialeDTO dto = credenzialeService.getById(id);
		assertFalse(dto.getAttivo());
	}

	@Test
	void testDisattivaFallito() {
		Integer missingId = 9999;
		credenzialeRepo.findById(missingId).ifPresent(credenzialeRepo::delete);

		CredenzialeReq req = new CredenzialeReq();
		req.setId(missingId);

		AcademyException exception = assertThrows(AcademyException.class, () -> credenzialeService.disattiva(req));
		assertTrue(exception.getMessage().contains("credenziale-non-trovata"));
	}

	@Test
	void testRiattivaSuccesso() throws AcademyException {
		String email = "enable@example.com";
		credenzialeRepo.findByEmail(normalizza(email)).ifPresent(credenzialeRepo::delete);

		CredenzialeDTO dtoCreato = credenzialeService.crea(createReq(email, "pass123"));
		Integer id = dtoCreato.getId();

		CredenzialeReq req = new CredenzialeReq();
		req.setId(id);
		credenzialeService.disattiva(req);

		credenzialeService.riattiva(req);
		CredenzialeDTO dto = credenzialeService.getById(id);
		assertTrue(dto.getAttivo());
	}

	@Test
	void testRiattivaFallito() {
		Integer missingId = 9999;
		credenzialeRepo.findById(missingId).ifPresent(credenzialeRepo::delete);

		CredenzialeReq req = new CredenzialeReq();
		req.setId(missingId);

		AcademyException exception = assertThrows(AcademyException.class, () -> credenzialeService.riattiva(req));
		assertTrue(exception.getMessage().contains("credenziale-non-trovata"));
	}

	@Test
	void testSignInSuccesso() throws AcademyException {
		String email = "signin@example.com";
		credenzialeRepo.findByEmail(normalizza(email)).ifPresent(credenzialeRepo::delete);

		credenzialeService.crea(createReq(email, "mypassword"));

		CredenzialeReq req = new CredenzialeReq();
		req.setEmail(email);
		req.setPassword("mypassword");

		CredenzialeDTO dto = credenzialeService.signIn(req);
		assertNotNull(dto.getUltimoLogin());
		assertEquals(normalizza(email), dto.getEmail());
	}

	@Test
	void testSignInFallitoCredenzialiNonValide() {
		CredenzialeReq req = new CredenzialeReq();
		req.setEmail("inesistente@example.com");
		req.setPassword("wrongpass");

		AcademyException exception = assertThrows(AcademyException.class, () -> credenzialeService.signIn(req));
		assertTrue(exception.getMessage().contains("credenziali-non-valide"));
	}

	@Test
	void testSignInAccountNonAttivo() throws AcademyException {
		String email = "inactive@example.com";
		credenzialeRepo.findByEmail(normalizza(email)).ifPresent(credenzialeRepo::delete);

		CredenzialeDTO dtoCreato = credenzialeService.crea(createReq(email, "pass123"));
		Integer id = dtoCreato.getId();

		CredenzialeReq reqDisattiva = new CredenzialeReq();
		reqDisattiva.setId(id);
		credenzialeService.disattiva(reqDisattiva);

		CredenzialeReq reqSignIn = new CredenzialeReq();
		reqSignIn.setEmail(email);
		reqSignIn.setPassword("pass123");

		AcademyException exception = assertThrows(AcademyException.class, () -> credenzialeService.signIn(reqSignIn));
		assertTrue(exception.getMessage().contains("account-non-attivo"));
	}

	@Test
	void testGetByIdFallito() {
		AcademyException exception = assertThrows(AcademyException.class, () -> credenzialeService.getById(9999));
		assertTrue(exception.getMessage().contains("credenziale-non-trovata"));
	}

	@Test
	void testListActiveSuccesso() {
		Credenziale c1 = new Credenziale();
		c1.setEmail(normalizza("active1@example.com"));
		c1.setPassword("pass1");
		c1.setAttivo(true);
		credenzialeRepo.saveAndFlush(c1);

		Credenziale c2 = new Credenziale();
		c2.setEmail(normalizza("active2@example.com"));
		c2.setPassword("pass2");
		c2.setAttivo(true);
		credenzialeRepo.saveAndFlush(c2);

		List<CredenzialeDTO> lista = credenzialeService.listActive();
		assertFalse(lista.isEmpty());
		assertTrue(lista.stream().allMatch(CredenzialeDTO::getAttivo));
	}
}
