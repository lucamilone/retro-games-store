package com.betacom.retrogames.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.betacom.retrogames.request.CarrelloRigaReq;
import com.betacom.retrogames.response.ResponseBase;

@Transactional
@SpringBootTest
public class CarrelloRigaControllerTest {

	@Autowired
	private CarrelloRigaController controller;

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

	private CarrelloRigaReq createReq(Integer carrelloId, Integer prodottoId, Integer quantita) {
		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setCarrelloId(carrelloId);
		req.setProdottoId(prodottoId);
		req.setQuantita(quantita);
		return req;
	}

	@Test
	void testAddProductSuccesso() {
		Account account = createAccount("Mario", "Rossi");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU1", "NES", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);
		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = createReq(carrello.getId(), prodotto.getId(), 2);

		ResponseBase res = controller.addProduct(req);

		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("successo"));
	}

	@Test
	void testAddProductFallito() {
		CarrelloRigaReq req = createReq(9999, 8888, 2);

		ResponseBase res = controller.addProduct(req);

		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
	}

	@Test
	void testUpdateRowSuccesso() throws AcademyException {
		Account account = createAccount("Luca", "Bianchi");
		Categoria categoria = createCategoria("Giochi");
		Prodotto prodotto = createProdotto("SKU2", "SNES", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodotto);
		riga.setQuantita(2);
		carrello.addRiga(riga);

		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(riga.getId());
		req.setQuantita(5);

		ResponseBase res = controller.updateRow(req);

		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("aggiornata"));
	}

	@Test
	void testUpdateRowFallito() {
		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(9999);
		req.setQuantita(5);

		ResponseBase res = controller.updateRow(req);

		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
	}

	@Test
	void testRemoveProductSuccesso() {
		Account account = createAccount("Paolo", "Verdi");
		Categoria categoria = createCategoria("Retro");
		Prodotto prodotto = createProdotto("SKU3", "Atari", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodotto);
		riga.setQuantita(1);
		carrello.addRiga(riga);

		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(riga.getId());

		ResponseBase res = controller.removeProduct(req);

		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("successo"));
	}

	@Test
	void testRemoveProductFallito() {
		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(9999);

		ResponseBase res = controller.removeProduct(req);

		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
	}
}
