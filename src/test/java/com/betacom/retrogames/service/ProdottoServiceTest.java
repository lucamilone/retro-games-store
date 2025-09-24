package com.betacom.retrogames.service;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Categoria;
import com.betacom.retrogames.model.Piattaforma;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.repository.ProdottoRepository;
import com.betacom.retrogames.request.ProdottoReq;
import com.betacom.retrogames.service.interfaces.ProdottoService;

@Transactional
@SpringBootTest
public class ProdottoServiceTest {
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private ProdottoService prodottoService;

	@Autowired
	private ProdottoRepository prodottoRepo;

	@Autowired
	private CategoriaRepository categoriaRepo;

	@Autowired
	private PiattaformaRepository piattaformaRepo;

	private ProdottoReq createReq(Integer categoriaId, String sku) {
		ProdottoReq req = new ProdottoReq();
		req.setNome("Prodotto Test");
		req.setDescrizione("Descrizione Test");
		req.setSku(normalizza(sku));
		req.setAnnoUscita(2001);
		req.setPrezzo(new BigDecimal("59.99"));
		req.setCategoriaId(categoriaId);
		return req;
	}

	private void cleanupProdottoBySku(String sku) {
		prodottoRepo.findBySku(normalizza(sku)).ifPresent(prodottoRepo::delete);
	}

	@Test
	void testCreaSuccesso() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Accessori");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		String skuConImg = "SKU-TEST-002";
		cleanupProdottoBySku(skuConImg);
		ProdottoReq reqConImg = createReq(categoria.getId(), skuConImg);
		reqConImg.setImgUrl("http://example.com/img.png");

		ProdottoDTO dtoConImg = prodottoService.crea(reqConImg);
		assertNotNull(dtoConImg);
		Integer idConImg = dtoConImg.getId();

		ProdottoDTO dtoFromDbConImg = prodottoService.getById(idConImg);
		assertEquals("Prodotto Test", dtoFromDbConImg.getNome());
		assertEquals(normalizza(skuConImg), dtoFromDbConImg.getSku());
		assertEquals(categoria.getNome(), dtoFromDbConImg.getCategoria());
		assertEquals("http://example.com/img.png", dtoFromDbConImg.getImgUrl());
		assertTrue(dtoFromDbConImg.getAttivo());

		String skuSenzaImg = "SKU-TEST-003";
		cleanupProdottoBySku(skuSenzaImg);
		ProdottoReq reqSenzaImg = createReq(categoria.getId(), skuSenzaImg);
		reqSenzaImg.setImgUrl(null);

		ProdottoDTO dtoSenzaImg = prodottoService.crea(reqSenzaImg);
		assertNotNull(dtoSenzaImg);
		Integer idSenzaImg = dtoSenzaImg.getId();

		ProdottoDTO dtoFromDbSenzaImg = prodottoService.getById(idSenzaImg);
		assertEquals("Prodotto Test", dtoFromDbSenzaImg.getNome());
		assertEquals(normalizza(skuSenzaImg), dtoFromDbSenzaImg.getSku());
		assertEquals(categoria.getNome(), dtoFromDbSenzaImg.getCategoria());
		assertEquals(null, dtoFromDbSenzaImg.getImgUrl());
		assertTrue(dtoFromDbSenzaImg.getAttivo());
	}

	@Test
	void testCreaFallito() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Accessori");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		String sku1 = "SKU-TEST-004";
		cleanupProdottoBySku(sku1);
		ProdottoReq req1 = createReq(categoria.getId(), sku1);
		prodottoService.crea(req1);

		ProdottoReq reqSkuDuplicato = createReq(categoria.getId(), sku1);
		AcademyException exSku = assertThrows(AcademyException.class, () -> prodottoService.crea(reqSkuDuplicato));
		assertTrue(exSku.getMessage().contains("prodotto-sku-esistente"));

		String skuNoCache = "SKU-TEST-005";
		cleanupProdottoBySku(skuNoCache);
		ProdottoReq reqNoCache = createReq(categoria.getId(), skuNoCache);
		cacheManager.removeRecordFromCachedTable(TabellaCostante.CATEGORIA, categoria.getId());

		AcademyException exCache = assertThrows(AcademyException.class, () -> prodottoService.crea(reqNoCache));
		assertTrue(exCache.getMessage().contains("categoria-non-trovata"));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Console");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		Categoria nuovaCategoria = new Categoria();
		nuovaCategoria.setNome("Accessori");
		nuovaCategoria.setAttivo(true);
		nuovaCategoria = categoriaRepo.saveAndFlush(nuovaCategoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(nuovaCategoria));

		String skuIniziale = "SKU-TEST-100";
		prodottoRepo.findBySku(normalizza(skuIniziale)).ifPresent(prodottoRepo::delete);
		ProdottoReq reqIniziale = createReq(categoria.getId(), skuIniziale);
		ProdottoDTO dtoCreato = prodottoService.crea(reqIniziale);
		assertNotNull(dtoCreato);
		Integer prodottoId = dtoCreato.getId();

		ProdottoDTO dtoIniziale = prodottoService.getById(prodottoId);
		assertEquals("Prodotto Test", dtoIniziale.getNome());
		assertEquals(normalizza(skuIniziale), dtoIniziale.getSku());
		assertEquals(categoria.getNome(), dtoIniziale.getCategoria());
		assertEquals(new BigDecimal("59.99"), dtoIniziale.getPrezzo());

		ProdottoReq reqAggiornamento = new ProdottoReq();
		reqAggiornamento.setId(prodottoId);
		reqAggiornamento.setSku("SKU-UPDATED");
		reqAggiornamento.setNome("Prodotto Aggiornato");
		reqAggiornamento.setCategoriaId(nuovaCategoria.getId());
		reqAggiornamento.setDescrizione("Descrizione Aggiornata");
		reqAggiornamento.setAnnoUscita(2022);
		reqAggiornamento.setPrezzo(new BigDecimal("99.99"));
		reqAggiornamento.setImgUrl("http://example.com/updated.png");
		reqAggiornamento.setAttivo(false);

		prodottoService.aggiorna(reqAggiornamento);

		ProdottoDTO dtoAggiornato = prodottoService.getById(prodottoId);
		assertEquals(normalizza("SKU-UPDATED"), dtoAggiornato.getSku());
		assertEquals("Prodotto Aggiornato", dtoAggiornato.getNome());
		assertEquals(nuovaCategoria.getNome(), dtoAggiornato.getCategoria());
		assertEquals("Descrizione Aggiornata", dtoAggiornato.getDescrizione());
		assertEquals(2022, dtoAggiornato.getAnnoUscita());
		assertEquals(new BigDecimal("99.99"), dtoAggiornato.getPrezzo());
		assertEquals("http://example.com/updated.png", dtoAggiornato.getImgUrl());
		assertEquals(false, dtoAggiornato.getAttivo());

		ProdottoReq reqAggiornamentoParziale = new ProdottoReq();
		reqAggiornamentoParziale.setId(prodottoId);
		reqAggiornamentoParziale.setNome("Nuovo Nome");
		reqAggiornamentoParziale.setPrezzo(new BigDecimal("129.99"));

		prodottoService.aggiorna(reqAggiornamentoParziale);

		ProdottoDTO dtoAggiornatoParziale = prodottoService.getById(prodottoId);
		assertEquals("Nuovo Nome", dtoAggiornatoParziale.getNome());
		assertEquals(new BigDecimal("129.99"), dtoAggiornatoParziale.getPrezzo());
		assertEquals(normalizza("SKU-UPDATED"), dtoAggiornatoParziale.getSku());
		assertEquals("Descrizione Aggiornata", dtoAggiornatoParziale.getDescrizione());

		ProdottoReq reqAggiornamentoSenzaPrezzo = new ProdottoReq();
		reqAggiornamentoSenzaPrezzo.setId(prodottoId);
		reqAggiornamentoSenzaPrezzo.setNome("Nome Senza Prezzo");

		prodottoService.aggiorna(reqAggiornamentoSenzaPrezzo);

		ProdottoDTO dtoAggiornatoSenzaPrezzo = prodottoService.getById(prodottoId);
		assertEquals("Nome Senza Prezzo", dtoAggiornatoSenzaPrezzo.getNome());
		assertEquals(new BigDecimal("129.99"), dtoAggiornatoSenzaPrezzo.getPrezzo());
		assertEquals(normalizza("SKU-UPDATED"), dtoAggiornatoSenzaPrezzo.getSku());
		assertEquals("Descrizione Aggiornata", dtoAggiornatoSenzaPrezzo.getDescrizione());
	}

	@Test
	void testAggiornaFallito() throws AcademyException {
		ProdottoReq reqNonEsistente = new ProdottoReq();
		reqNonEsistente.setId(9999);
		reqNonEsistente.setNome("Nome Test");

		AcademyException exProdottoNonEsistente = assertThrows(AcademyException.class,
				() -> prodottoService.aggiorna(reqNonEsistente));
		assertTrue(exProdottoNonEsistente.getMessage().contains("prodotto-non-trovato"));

		Categoria categoria = new Categoria();
		categoria.setNome("Console");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		String skuValido = "SKU-TEST-200";
		prodottoRepo.findBySku(normalizza(skuValido)).ifPresent(prodottoRepo::delete);
		ProdottoReq reqIniziale = createReq(categoria.getId(), skuValido);
		ProdottoDTO dtoCreato = prodottoService.crea(reqIniziale);
		assertNotNull(dtoCreato);
		Integer prodottoId = dtoCreato.getId();

		ProdottoReq reqCategoriaInvalida = new ProdottoReq();
		reqCategoriaInvalida.setId(prodottoId);
		reqCategoriaInvalida.setCategoriaId(9999);

		AcademyException exCategoriaNonTrovata = assertThrows(AcademyException.class,
				() -> prodottoService.aggiorna(reqCategoriaInvalida));
		assertTrue(exCategoriaNonTrovata.getMessage().contains("categoria-non-trovata"));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Accessori");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		String skuProdotto = "SKU-DISATTIVA-001";
		prodottoRepo.findBySku(normalizza(skuProdotto)).ifPresent(prodottoRepo::delete);

		ProdottoReq reqIniziale = createReq(categoria.getId(), skuProdotto);
		ProdottoDTO dtoCreato = prodottoService.crea(reqIniziale);
		assertNotNull(dtoCreato);
		Integer prodottoId = dtoCreato.getId();

		ProdottoReq reqDisattiva = new ProdottoReq();
		reqDisattiva.setId(prodottoId);
		prodottoService.disattiva(reqDisattiva);

		ProdottoDTO dtoDisattivato = prodottoService.getById(prodottoId);
		assertEquals(false, dtoDisattivato.getAttivo());
	}

	@Test
	void testDisattivaFallito() {
		ProdottoReq reqNonEsistente = new ProdottoReq();
		reqNonEsistente.setId(9999);

		AcademyException ex = assertThrows(AcademyException.class, () -> prodottoService.disattiva(reqNonEsistente));
		assertTrue(ex.getMessage().contains("prodotto-non-trovato"));
	}

	@Test
	void testGetByIdSuccesso() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Console");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		String skuProdotto = "SKU-GETBYID-001";
		prodottoRepo.findBySku(normalizza(skuProdotto)).ifPresent(prodottoRepo::delete);

		ProdottoReq req = createReq(categoria.getId(), skuProdotto);
		ProdottoDTO dtoCreato = prodottoService.crea(req);
		assertNotNull(dtoCreato);
		Integer prodottoId = dtoCreato.getId();

		ProdottoDTO dto = prodottoService.getById(prodottoId);
		assertEquals("Prodotto Test", dto.getNome());
		assertEquals(normalizza(skuProdotto), dto.getSku());
		assertEquals(categoria.getNome(), dto.getCategoria());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testGetByIdFallito() {
		AcademyException ex = assertThrows(AcademyException.class, () -> prodottoService.getById(9999));
		assertTrue(ex.getMessage().contains("prodotto-non-trovato"));
	}

	@Test
	void testListActiveSuccesso() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Console");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		ProdottoDTO dto1 = prodottoService.crea(createReq(categoria.getId(), "SKU-ACTIVE-001"));
		ProdottoDTO dto2 = prodottoService.crea(createReq(categoria.getId(), "SKU-ACTIVE-002"));
		Integer id1 = dto1.getId();
		Integer id2 = dto2.getId();

		List<ProdottoDTO> listaAttivi = prodottoService.listActive();

		assertTrue(listaAttivi.stream().anyMatch(p -> p.getId().equals(id1)));
		assertTrue(listaAttivi.stream().anyMatch(p -> p.getId().equals(id2)));
		assertTrue(listaAttivi.stream().allMatch(ProdottoDTO::getAttivo));
	}

	@Test
	void testValidatePiattaformeVideogiocoConPiattaforme() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Videogiochi");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		ProdottoReq req = createReq(categoria.getId(), "SKU-VID-001");
		req.setPiattaformaId(new HashSet<>(Arrays.asList(1, 2)));

		ProdottoDTO dtoCreato = prodottoService.crea(req);
		assertNotNull(dtoCreato);
		Integer id = dtoCreato.getId();
		ProdottoDTO dto = prodottoService.getById(id);

		assertEquals(2, dto.getPiattaforme().size());
	}

	@Test
	void testValidatePiattaformeVideogiocoSenzaPiattaforme() {
		Categoria categoria = new Categoria();
		categoria.setNome("Videogiochi");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		ProdottoReq req = createReq(categoria.getId(), "SKU-VID-002");
		req.setPiattaformaId(null);

		AcademyException ex = assertThrows(AcademyException.class, () -> prodottoService.crea(req));
		assertTrue(ex.getMessage().contains("piattaforma-videogioco-obbligatoria"));
	}

	@Test
	void testValidatePiattaformeVideogiocoPiattaformeVuote() {
		Categoria categoria = new Categoria();
		categoria.setNome("Videogiochi");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		ProdottoReq req = createReq(categoria.getId(), "SKU-VID-003");
		req.setPiattaformaId(new HashSet<>());

		AcademyException ex = assertThrows(AcademyException.class, () -> prodottoService.crea(req));
		assertTrue(ex.getMessage().contains("piattaforma-videogioco-obbligatoria"));
	}

	@Test
	void testValidatePiattaformeCategoriaNonVideogioco() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Accessori");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		ProdottoReq req = createReq(categoria.getId(), "SKU-ACC-001");
		req.setPiattaformaId(new HashSet<>(Arrays.asList(1, 2)));

		ProdottoDTO dtoCreato = prodottoService.crea(req);
		assertNotNull(dtoCreato);
		Integer id = dtoCreato.getId();
		ProdottoDTO dto = prodottoService.getById(id);

		assertTrue(dto.getPiattaforme().isEmpty());
	}

	@Test
	void testValidatePiattaformeCategoriaNonSupportata() {
		Categoria categoria = new Categoria();
		categoria.setNome("NonSupportata");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		ProdottoReq req = createReq(categoria.getId(), "SKU-NS-001");

		AcademyException ex = assertThrows(AcademyException.class, () -> prodottoService.crea(req));

		assertTrue(ex.getMessage().contains("categoria-non-supportata"));
	}

	@Test
	void testcheckCachedPiattaformeSuccesso() throws AcademyException {
		Categoria categoria = new Categoria();
		categoria.setNome("Videogiochi");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		Piattaforma piattaforma1 = new Piattaforma();
		piattaforma1.setNome("PlayStation-123");
		piattaforma1.setCodice("PS");
		piattaforma1.setAttivo(true);
		piattaforma1.setAnnoUscitaPiattaforma(1994);
		piattaforma1 = piattaformaRepo.saveAndFlush(piattaforma1);

		Piattaforma piattaforma2 = new Piattaforma();
		piattaforma2.setNome("Xbox-456");
		piattaforma2.setCodice("XBX");
		piattaforma2.setAttivo(true);
		piattaforma2.setAnnoUscitaPiattaforma(2001);
		piattaforma2 = piattaformaRepo.saveAndFlush(piattaforma2);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, new CachedPiattaforma(piattaforma1));
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, new CachedPiattaforma(piattaforma2));

		ProdottoReq req = createReq(categoria.getId(), "SKU-PLT-SUCCESSO");
		req.setPiattaformaId(new HashSet<>(Arrays.asList(piattaforma1.getId(), piattaforma2.getId())));

		ProdottoDTO dtoCreato = prodottoService.crea(req);
		assertNotNull(dtoCreato);
		Integer prodottoId = dtoCreato.getId();
		ProdottoDTO dto = prodottoService.getById(prodottoId);

		assertEquals(2, dto.getPiattaforme().size());
		assertTrue(dto.getPiattaforme().stream().anyMatch(p -> p.getNome().equals("PlayStation-123")));
		assertTrue(dto.getPiattaforme().stream().anyMatch(p -> p.getNome().equals("Xbox-456")));
	}

	@Test
	void testCheckCachedPiattaformeFallito() {
		Categoria categoria = new Categoria();
		categoria.setNome("Videogiochi");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		ProdottoReq req = createReq(categoria.getId(), "SKU-PLT-FALLITO");
		req.setPiattaformaId(new HashSet<>(Arrays.asList(9999)));

		AcademyException ex = assertThrows(AcademyException.class, () -> prodottoService.crea(req));
		assertTrue(ex.getMessage().contains("piattaforma-non-trovata"));
	}

	@Test
	void testCheckCachedPiattaformeRamoNonTrovataDB() {
		Categoria categoria = new Categoria();
		categoria.setNome("Videogiochi");
		categoria.setAttivo(true);
		categoria = categoriaRepo.saveAndFlush(categoria);

		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

		Piattaforma fakePiattaforma = new Piattaforma();
		fakePiattaforma.setId(9999);
		fakePiattaforma.setNome("Fake");
		fakePiattaforma.setCodice("FAKE");
		fakePiattaforma.setAttivo(true);
		fakePiattaforma.setAnnoUscitaPiattaforma(2025);

		CachedPiattaforma cachedFake = new CachedPiattaforma(fakePiattaforma);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, cachedFake);

		ProdottoReq req = createReq(categoria.getId(), "SKU-PLT-FALLITO");
		req.setPiattaformaId(new HashSet<>(Arrays.asList(9999)));

		AcademyException ex = assertThrows(AcademyException.class, () -> prodottoService.crea(req));

		assertTrue(ex.getMessage().contains("piattaforma-non-trovata"));
	}
}
