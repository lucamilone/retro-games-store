package com.betacom.retrogames.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.CarrelloRigaDTO;
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
import com.betacom.retrogames.repository.CarrelloRigaRepository;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.repository.ProdottoRepository;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.CarrelloRigaReq;
import com.betacom.retrogames.service.interfaces.CarrelloRigaService;

@Transactional
@SpringBootTest
public class CarrelloRigaServiceTest {

	@Autowired
	private CarrelloRigaService carrelloRigaService;

	@Autowired
	private CarrelloRigaRepository carrelloRigaRepo;

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

	private CarrelloRigaReq createReq(int carrelloId, int prodottoId, int quantita) {
		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setCarrelloId(carrelloId);
		req.setProdottoId(prodottoId);
		req.setQuantita(quantita);
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
	void testAggiungiStessoProdottoSuccesso() throws AcademyException {
		Account account = createAccount("Luigi", "Verdi");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU456", "GameBoy", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodotto);
		riga.setQuantita(1);
		carrello.addRiga(riga);
		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req1 = createReq(carrello.getId(), prodotto.getId(), 2);
		carrelloRigaService.aggiungiProdotto(req1);

		Carrello carrelloAggiornato1 = carrelloRepo.findById(carrello.getId()).orElseThrow();
		assertEquals(1, carrelloAggiornato1.getRighe().size());
		CarrelloRiga rigaAgg1 = carrelloAggiornato1.getRighe().get(0);
		assertEquals(3, rigaAgg1.getQuantita()); // 1 + 2
		assertEquals(BigDecimal.valueOf(300), rigaAgg1.getSubTotale());

		CarrelloRigaReq req2 = createReq(carrello.getId(), prodotto.getId(), 1);
		carrelloRigaService.aggiungiProdotto(req2);

		Carrello carrelloAggiornato2 = carrelloRepo.findById(carrello.getId()).orElseThrow();
		assertEquals(1, carrelloAggiornato2.getRighe().size());
		CarrelloRiga rigaAgg2 = carrelloAggiornato2.getRighe().get(0);
		assertEquals(4, rigaAgg2.getQuantita()); // 3 + 1
		assertEquals(BigDecimal.valueOf(400), rigaAgg2.getSubTotale());
	}

	@Test
	void testAggiungiNuovoProdottoSuccesso() throws AcademyException {
		Account account = createAccount("Anna", "Rossi");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU789", "Atari 2600", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);
		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = createReq(carrello.getId(), prodotto.getId(), 2);
		carrelloRigaService.aggiungiProdotto(req);

		Carrello carrelloAggiornato = carrelloRepo.findById(carrello.getId()).orElseThrow();
		assertEquals(1, carrelloAggiornato.getRighe().size());
		CarrelloRiga rigaAgg = carrelloAggiornato.getRighe().get(0);
		assertEquals(prodotto.getId(), rigaAgg.getProdotto().getId());
		assertEquals(2, rigaAgg.getQuantita());
		assertEquals(BigDecimal.valueOf(200), rigaAgg.getSubTotale());
	}

	@Test
	void testAggiungiProdottoCarrelloENonTrovato() {
		CarrelloRigaReq req = createReq(9999, 1, 1);

		assertThrows(AcademyException.class, () -> carrelloRigaService.aggiungiProdotto(req));
	}

	@Test
	void testAggiungiProdottoProdottoNonTrovato() throws AcademyException {
		Account account = createAccount("Marco", "Neri");
		Carrello carrello = new Carrello();
		carrello.setAccount(account);
		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = createReq(carrello.getId(), 9999, 1);

		assertThrows(AcademyException.class, () -> carrelloRigaService.aggiungiProdotto(req));
	}

	@Test
	void testAggiornaRigaSuccesso() throws AcademyException {
		Account account = createAccount("Anna", "Neri");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU789", "Atari", categoria);

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

		carrelloRigaService.aggiornaRiga(req);

		CarrelloRiga rigaAggiornata = carrelloRigaRepo.findById(riga.getId()).orElseThrow();
		assertEquals(5, rigaAggiornata.getQuantita());
		assertEquals(prodotto.getId(), rigaAggiornata.getProdotto().getId());
	}

	@Test
	void testAggiornaRigaNonTrovataLanciaEccezione() {
		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(999999);
		req.setQuantita(1);
		assertThrows(AcademyException.class, () -> carrelloRigaService.aggiornaRiga(req));
	}

	@Test
	void testAggiornaRigaCambiaProdottoConMergeQuantita() throws AcademyException {
		Account account = createAccount("Mario", "Bianchi");
		Categoria categoria = createCategoria("Giochi");
		Prodotto prodA = createProdotto("SKU-A", "NES", categoria);
		Prodotto prodB = createProdotto("SKU-B", "SNES", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga rigaA = new CarrelloRiga();
		rigaA.setProdotto(prodA);
		rigaA.setQuantita(2);
		carrello.addRiga(rigaA);

		CarrelloRiga rigaB = new CarrelloRiga();
		rigaB.setProdotto(prodB);
		rigaB.setQuantita(3);
		carrello.addRiga(rigaB);

		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(rigaA.getId());
		req.setProdottoId(prodB.getId());
		req.setQuantita(5);

		carrelloRigaService.aggiornaRiga(req);

		boolean rigaAEsiste = carrelloRigaRepo.findById(rigaA.getId()).isPresent();
		CarrelloRiga rigaBPost = carrelloRigaRepo.findById(rigaB.getId()).orElseThrow();
		assertFalse(rigaAEsiste);
		assertEquals(8, rigaBPost.getQuantita());
		assertEquals(prodB.getId(), rigaBPost.getProdotto().getId());
	}

	@Test
	void testAggiornaRigaMergeQuantitaReqNull() throws AcademyException {
		Account account = createAccount("Fabio", "Verdi");
		Categoria categoria = createCategoria("Giochi");
		Prodotto prodA = createProdotto("SKU-A1", "NES", categoria);
		Prodotto prodB = createProdotto("SKU-B1", "SNES", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga rigaA = new CarrelloRiga();
		rigaA.setProdotto(prodA);
		rigaA.setQuantita(2);
		carrello.addRiga(rigaA);

		CarrelloRiga rigaB = new CarrelloRiga();
		rigaB.setProdotto(prodB);
		rigaB.setQuantita(3);
		carrello.addRiga(rigaB);

		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(rigaA.getId());
		req.setProdottoId(prodB.getId());
		req.setQuantita(null);

		carrelloRigaService.aggiornaRiga(req);

		boolean rigaAEsiste = carrelloRigaRepo.findById(rigaA.getId()).isPresent();
		CarrelloRiga rigaBPost = carrelloRigaRepo.findById(rigaB.getId()).orElseThrow();
		assertFalse(rigaAEsiste);
		assertEquals(5, rigaBPost.getQuantita());
		assertEquals(prodB.getId(), rigaBPost.getProdotto().getId());
	}

	@Test
	void testAggiornaRigaCambiaProdottoSenzaMergeAggiornaSoloProdotto() throws AcademyException {
		Account account = createAccount("Sara", "Blu");
		Categoria categoria = createCategoria("Console");
		Prodotto prodA = createProdotto("SKU-1", "MegaDrive", categoria);
		Prodotto prodNuovo = createProdotto("SKU-2", "Saturn", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodA);
		riga.setQuantita(2);
		carrello.addRiga(riga);

		carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(riga.getId());
		req.setProdottoId(prodNuovo.getId());

		carrelloRigaService.aggiornaRiga(req);

		CarrelloRiga rigaAgg = carrelloRigaRepo.findById(riga.getId()).orElseThrow();
		assertEquals(prodNuovo.getId(), rigaAgg.getProdotto().getId());
		assertEquals(2, rigaAgg.getQuantita());
	}

	@Test
	void testAggiornaRigaQuantitaZeroEliminaRiga() throws AcademyException {
		Account account = createAccount("Giulia", "Neri");
		Categoria categoria = createCategoria("Retro");
		Prodotto prodotto = createProdotto("SKU-Z", "ZX Spectrum", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodotto);
		riga.setQuantita(2);
		carrello.addRiga(riga);

		carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(riga.getId());
		req.setQuantita(0);

		carrelloRigaService.aggiornaRiga(req);

		assertTrue(carrelloRigaRepo.findById(riga.getId()).isEmpty());
	}

	@Test
	void testAggiornaRigaProdottoStessoNonMergeQuantita() throws AcademyException {
		Account account = createAccount("Luca", "Bianchi");
		Categoria categoria = createCategoria("Giochi");
		Prodotto prodotto = createProdotto("SKU-TEST", "TestGame", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodotto);
		riga.setQuantita(3);
		carrello.addRiga(riga);
		carrello = carrelloRepo.saveAndFlush(carrello);

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(riga.getId());
		req.setProdottoId(prodotto.getId());
		req.setQuantita(5);

		carrelloRigaService.aggiornaRiga(req);

		CarrelloRiga rigaAgg = carrelloRigaRepo.findById(riga.getId()).orElseThrow();
		assertEquals(5, rigaAgg.getQuantita());
		assertEquals(prodotto.getId(), rigaAgg.getProdotto().getId());
	}

	@Test
	void testAggiornaRigaProdottoIdNull() throws AcademyException {
		Account account = createAccount("Laura", "Rossi");
		Categoria categoria = createCategoria("Console");
		Prodotto prodotto = createProdotto("SKU-NULL", "NullGame", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodotto);
		riga.setQuantita(2);
		carrello.addRiga(riga);
		carrello = carrelloRepo.saveAndFlush(carrello);

		// Req con prodottoId null
		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(riga.getId());
		req.setProdottoId(null);
		req.setQuantita(7);

		carrelloRigaService.aggiornaRiga(req);

		CarrelloRiga rigaAgg = carrelloRigaRepo.findById(riga.getId()).orElseThrow();
		assertEquals(7, rigaAgg.getQuantita());
		assertEquals(prodotto.getId(), rigaAgg.getProdotto().getId());
	}

	@Test
	void testRimuoviProdottoNonTrovatoLanciaEccezione() {
		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(123456);
		assertThrows(AcademyException.class, () -> carrelloRigaService.rimuoviProdotto(req));
	}

	@Test
	void testRimuoviProdottoSuccesso() throws AcademyException {
		Account account = createAccount("Paolo", "Rossi");
		Categoria categoria = createCategoria("Varie");
		Prodotto prodotto = createProdotto("SKU-R", "PS1", categoria);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga riga = new CarrelloRiga();
		riga.setProdotto(prodotto);
		riga.setQuantita(1);
		carrello.addRiga(riga);

		carrello = carrelloRepo.saveAndFlush(carrello);
		int carrelloId = carrello.getId();

		CarrelloRigaReq req = new CarrelloRigaReq();
		req.setId(riga.getId());

		carrelloRigaService.rimuoviProdotto(req);

		carrelloRigaRepo.flush();

		Carrello carrelloPost = carrelloRepo.findById(carrelloId).orElseThrow();
		assertEquals(0, carrelloPost.getRighe().size());
		assertTrue(carrelloRigaRepo.findById(riga.getId()).isEmpty());
	}

	@Test
	void testListByCarrelloRestituisceDTOConSubTotali() throws AcademyException {
		Account account = createAccount("Elisa", "Gialli");
		Categoria categoria = createCategoria("Categorie");
		Prodotto p1 = createProdotto("SKU-10", "C64", categoria);
		Prodotto p2 = createProdotto("SKU-20", "Amiga", categoria);

		p2.setPrezzo(BigDecimal.valueOf(150));
		p2 = prodottoRepo.saveAndFlush(p2);

		Carrello carrello = new Carrello();
		carrello.setAccount(account);

		CarrelloRiga r1 = new CarrelloRiga();
		r1.setProdotto(p1);
		r1.setQuantita(1);
		carrello.addRiga(r1);

		CarrelloRiga r2 = new CarrelloRiga();
		r2.setProdotto(p2);
		r2.setQuantita(3);
		carrello.addRiga(r2);

		carrello = carrelloRepo.saveAndFlush(carrello);

		List<CarrelloRigaDTO> dtos = carrelloRigaService.listByCarrello(carrello.getId());
		assertEquals(2, dtos.size());
		assertEquals(BigDecimal.valueOf(100), dtos.get(0).getSubTotale());
		assertEquals(BigDecimal.valueOf(450), dtos.get(1).getSubTotale());
	}

	@Test
	void testListByCarrelloVuotoRestituisceListaVuota() throws AcademyException {
		Account account = createAccount("Luca", "Verdi");
		Carrello carrello = new Carrello();
		carrello.setAccount(account);
		carrello = carrelloRepo.saveAndFlush(carrello);

		List<CarrelloRigaDTO> dtos = carrelloRigaService.listByCarrello(carrello.getId());
		assertTrue(dtos.isEmpty());
	}
}
