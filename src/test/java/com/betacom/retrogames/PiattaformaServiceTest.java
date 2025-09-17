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

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Piattaforma;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.service.interfaces.PiattaformaService;

@SpringBootTest
@Transactional
class PiattaformaServiceTest 
{
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private PiattaformaService piattaformaS;

	@Autowired
	private PiattaformaRepository piattaformaRepo;

	@Test
	void testCreaSuccesso() throws AcademyException 
	{
		PiattaformaReq req = new PiattaformaReq();
		req.setNome("PlayStation 1");
		req.setCodice("ps1");
		req.setAnnoUscitaPiattaforma(1994);
		req.setAttivo(true);
		
		// Verifico che la piattaforma non esista già
		Piattaforma piattaformaEsistente = piattaformaRepo.findByCodice("ps1").orElse(null);
		if (piattaformaEsistente != null) 
		{
			piattaformaRepo.delete(piattaformaEsistente);
		}

		// Creo la piattaforma
		Integer id = piattaformaS.crea(req);

		// Verifico che l'id restituito non sia nullo
		assertNotNull(id);

		// Ottengo la piattaforma creata
		PiattaformaDTO dto = piattaformaS.getById(id);

		// Verifico che il nome e il codice siano salvati correttamente
		assertEquals("PlayStation 1", dto.getNome());
		assertEquals("ps1", dto.getCodice());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testCreaGiaEsistente() throws AcademyException 
	{
		PiattaformaReq req = new PiattaformaReq();
		req.setNome("PlayStation 1");
		req.setCodice("ps1");
		req.setAnnoUscitaPiattaforma(1994);
		req.setAttivo(true);

		// Verifico che la piattaforma esista già
		assertThrows(AcademyException.class, () -> piattaformaS.crea(req));
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException 
	{
		// Creo una piattaforma se non esiste
		Piattaforma piattaforma = piattaformaRepo.findByCodice("ps1").orElseThrow();
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, new CachedPiattaforma(piattaforma));
		
		// Preparo la richiesta di aggiornamento
		PiattaformaReq req = new PiattaformaReq();
		req.setId(piattaforma.getId());
		req.setNome("PlayStation 1 Aggiornata");

		// Aggiorno la piattaforma
		piattaformaS.aggiorna(req);

		// Verifico l'aggiornamento
		PiattaformaDTO updated = piattaformaS.getById(piattaforma.getId());
		assertEquals("PlayStation 1 Aggiornata", updated.getNome());
		assertTrue(updated.getAttivo());
	}

	@Test
	void testAggiornaNonEsistente() 
	{
		PiattaformaReq req = new PiattaformaReq();
		req.setId(99);
		req.setNome("FakePiattaforma");
		assertThrows(AcademyException.class, () -> piattaformaS.aggiorna(req));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException 
	{
	    // Creo una piattaforma di test con tutti i campi obbligatori
	    Piattaforma piattaforma = new Piattaforma();
	    piattaforma.setAnnoUscitaPiattaforma(1999);
	    piattaforma.setNome("Piattaforma di Test");
	    piattaforma.setAttivo(true);
	    piattaforma.setCodice("ABC123");
	    piattaforma = piattaformaRepo.save(piattaforma); 

	    // Aggiungo la piattaforma alla cache
	    cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, new CachedPiattaforma(piattaforma));

	    PiattaformaReq req = new PiattaformaReq();
	    req.setId(piattaforma.getId());  

	    // Disattivo la piattaforma
	    piattaformaS.disattiva(req);

	    // Verifico che la piattaforma sia stata disattivata correttamente
	    assertFalse(piattaforma.getAttivo());
	}

	@Test
	void testDisattivaNonEsistenteLanciaEccezione() 
	{
		PiattaformaReq req = new PiattaformaReq();
		req.setId(99);
		assertThrows(AcademyException.class, () -> piattaformaS.disattiva(req));
	}

	@Test
	void testListActive() 
	{
		List<PiattaformaDTO> lista = piattaformaS.listActive();

		// Verifico che la lista non sia vuota
		assertFalse(lista.isEmpty());

		// Verifico che tutte le piattaforme siano attive
		assertTrue(lista.stream().allMatch(PiattaformaDTO::getAttivo));
	}
}