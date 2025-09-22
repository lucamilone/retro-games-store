package com.betacom.retrogames.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.Carrello;
import com.betacom.retrogames.model.CarrelloRiga;
import com.betacom.retrogames.model.Categoria;
import com.betacom.retrogames.model.Indirizzo;
import com.betacom.retrogames.model.Prodotto;
import com.betacom.retrogames.model.Ruolo;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.CarrelloRepository;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.repository.ProdottoRepository;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.CarrelloReq;
import com.betacom.retrogames.service.interfaces.CarrelloService;

@Transactional
@SpringBootTest
public class CarrelloServiceTest {

	@Autowired
	private CarrelloService carrelloService;

	@Autowired
	private CarrelloRepository carrelloRepo;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private RuoloRepository ruoloRepo;

	@Autowired
	private CategoriaRepository categoriaRepo;

	@Autowired
	private ProdottoRepository prodottoRepo;

	private CarrelloReq createReq(Integer accountId) {
		CarrelloReq req = new CarrelloReq();
		req.setAccountId(accountId);
		return req;
	}

	private Account createAccount(String nome, String cognome) {
		Ruolo ruolo = new Ruolo();
		ruolo.setNome("USER_TEST");
		ruolo.setAttivo(true);
		ruolo = ruoloRepo.saveAndFlush(ruolo);

		Indirizzo indirizzo = new Indirizzo();
		indirizzo.setVia("Via Roma 1");
		indirizzo.setCitta("Roma");
		indirizzo.setCap("00100");
		indirizzo.setPaese("Italia");

		Account account = new Account();
		account.setNome(nome);
		account.setCognome(cognome);
		account.setAttivo(true);
		account.setRuolo(ruolo);
		account.setIndirizzo(indirizzo);

		return accountRepo.saveAndFlush(account);
	}

	private Categoria createCategoria(String nome) {
		Categoria categoria = new Categoria();
		categoria.setNome(nome);
		categoria.setAttivo(true);
		return categoriaRepo.saveAndFlush(categoria);
	}

	private Prodotto createProdotto(String sku, String nome, Categoria categoria) {
		Prodotto prodotto = new Prodotto();
		prodotto.setSku(sku);
		prodotto.setNome(nome);
		prodotto.setCategoria(categoria);
		prodotto.setDescrizione("Prodotto di test");
		prodotto.setAnnoUscita(1990);
		prodotto.setPrezzo(BigDecimal.valueOf(100));
		prodotto.setAttivo(true);
		return prodottoRepo.saveAndFlush(prodotto);
	}

	@Test
	void testCreaPerAccountSuccesso() throws AcademyException {
		Account account = createAccount("Mario", "Rossi");

		CarrelloReq req = new CarrelloReq();
		req.setAccountId(account.getId());

		int carrelloId = carrelloService.creaPerAccount(req);
		Carrello carrello = carrelloRepo.findById(carrelloId).orElse(null);

		assertNotNull(carrello);
		assertEquals(account.getId(), carrello.getAccount().getId());
	}

	@Test
	void testCreaPerAccountFallito() {
		CarrelloReq req = createReq(9999);

		AcademyException ex = assertThrows(AcademyException.class, () -> carrelloService.creaPerAccount(req));
		assertTrue(ex.getMessage().contains("account-non-trovato"));
	}

	@Test
	void testSvuotaCarrelloSuccesso() throws AcademyException {
		Account account = createAccount("Luca", "Verdi");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU-TEST-001", "NES Console", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setQuantita(1);
		riga.setCarrello(carrello);
		riga.setProdotto(prodotto);

		carrello.getRighe().add(riga);
		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloReq req = new CarrelloReq();
		req.setId(carrello.getId());

		carrelloService.svuotaCarrello(req);

		Carrello carrelloAggiornato = carrelloRepo.findById(carrello.getId()).get();
		assertTrue(carrelloAggiornato.getRighe().isEmpty());
	}

	@Test
	void testSvuotaCarrelloCarrelloFallito() {
		CarrelloReq req = new CarrelloReq();
		req.setId(999);

		AcademyException ex = assertThrows(AcademyException.class, () -> carrelloService.svuotaCarrello(req));
		assertTrue(ex.getMessage().contains("carrello-non-trovato"));
	}

	@Test
	void testGetCarrelloByAccountSuccesso() throws AcademyException {
		Account account = createAccount("Giovanni", "Bianchi");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU-TEST-002", "SNES Console", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setQuantita(2);
		riga.setCarrello(carrello);
		riga.setProdotto(prodotto);

		carrello.getRighe().add(riga);
		carrello = carrelloRepo.saveAndFlush(carrello);

		var dto = carrelloService.getCarrelloByAccount(account.getId());

		assertNotNull(dto);
		assertEquals(account.getId(), dto.getAccountId());
		assertEquals(1, dto.getRighe().size());
		assertEquals(2, dto.getTotaleQuantita());
		assertEquals(BigDecimal.valueOf(200), dto.getTotale());
	}

	@Test
	void testGetCarrelloByAccountFallito() {
		int accountIdInesistente = 9999;

		AcademyException ex = assertThrows(AcademyException.class,
				() -> carrelloService.getCarrelloByAccount(accountIdInesistente));

		assertTrue(ex.getMessage().contains("carrello-non-trovato-per-account"));
	}
}