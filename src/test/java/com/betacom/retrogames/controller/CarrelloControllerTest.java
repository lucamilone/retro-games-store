package com.betacom.retrogames.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.CarrelloDTO;
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
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseObject;

@Transactional
@SpringBootTest
public class CarrelloControllerTest {

	@Autowired
	private CarrelloController controller;

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
		prodotto.setAnnoUscita(2000);
		prodotto.setPrezzo(BigDecimal.valueOf(100));
		prodotto.setAttivo(true);
		return prodottoRepo.saveAndFlush(prodotto);
	}

	private CarrelloReq createReq(Integer carrelloId) {
		CarrelloReq req = new CarrelloReq();
		req.setId(carrelloId);
		return req;
	}

	@Test
	void testClearCartSuccesso() throws Exception {
		Account account = createAccount("Mario", "Rossi");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU_TEST_001", "NES Console", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setCarrello(carrello);
		riga.setProdotto(prodotto);
		riga.setQuantita(1);

		carrello.getRighe().add(riga);
		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloReq req = createReq(carrello.getId());
		ResponseBase res = controller.clearCart(req);

		assertTrue(res.getReturnCode());
		assertNotNull(res.getMsg());
		assertTrue(res.getMsg().toLowerCase().contains("svuotato"));
	}

	@Test
	void testClearCartFallito() {
		CarrelloReq req = createReq(99999);
		ResponseBase res = controller.clearCart(req);

		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
	}

	@Test
	void testGetCarrelloByAccountSuccesso() throws Exception {
		Account account = createAccount("Luca", "Verdi");
		Carrello carrello = new Carrello();
		carrello.setAccount(account);
		carrello = carrelloRepo.saveAndFlush(carrello);

		ResponseObject<CarrelloDTO> res = controller.getCarrelloByAccount(account.getId());

		assertTrue(res.getReturnCode());
		assertNotNull(res.getDati());
	}

	@Test
	void testGetCarrelloByAccountFallito() {
		ResponseObject<CarrelloDTO> res = controller.getCarrelloByAccount(99999);

		assertFalse(res.getReturnCode());
		assertNotNull(res.getMsg());
	}
}
