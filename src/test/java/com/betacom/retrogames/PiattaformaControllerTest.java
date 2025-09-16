package com.betacom.retrogames;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Piattaforma;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.service.interfaces.PiattaformaService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
public class PiattaformaControllerTest {
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private PiattaformaService piattaformaService;

	@Autowired
	private PiattaformaRepository piattaformaRepo;

	@Test
	void testCreaSuccesso() throws AcademyException {
		PiattaformaReq req = new PiattaformaReq();
		String nomePiattaforma = "PlayStation 1";

		req.setNome(nomePiattaforma);
		req.setCodice(normalizza("ps1"));
		req.setAnnoUscitaPiattaforma(1994);
		req.setAttivo(true);

		// Verifico che la piattaforma non esista già
		Optional<Piattaforma> piattaformaEsistente = piattaformaRepo.findByCodice(req.getCodice());
		if (piattaformaEsistente.isPresent()) {
			piattaformaRepo.delete(piattaformaEsistente.get());
		}

		Integer id = piattaformaService.crea(req);

		// Verifico che l'id restituito non sia nullo
		assertNotNull(id);

		// Ottengo la piattaforma creata
		PiattaformaDTO dto = piattaformaService.getById(id);

		// Verifico che il nome e il codice siano salvati come ci aspettiamo e che la piattaforma sia attiva
		assertEquals(nomePiattaforma, dto.getNome());
		assertTrue(dto.getAttivo());
	}

	@Test
	void testAggiornaSuccesso() throws AcademyException {
		// Creazione della piattaforma se non esiste
		String codice = "ps1";
		Optional<Piattaforma> piattaformaOpt = piattaformaRepo.findByCodice(codice);

		Piattaforma piattaforma;
		if (piattaformaOpt.isEmpty()) {
			// Creo la piattaforma iniziale
			PiattaformaReq reqCreate = new PiattaformaReq();
			reqCreate.setNome("PlayStation 1");
			reqCreate.setCodice(normalizza(codice));
			reqCreate.setAnnoUscitaPiattaforma(1994);
			reqCreate.setAttivo(true);

			Integer id = piattaformaService.crea(reqCreate);
			piattaforma = piattaformaRepo.findById(id)
					.orElseThrow(() -> new RuntimeException("Impossibile creare la piattaforma per il test"));
		} else {
			piattaforma = piattaformaOpt.get();
		}

		// Aggiorno la piattaforma
		PiattaformaReq reqUpdate = new PiattaformaReq();
		reqUpdate.setId(piattaforma.getId());
		reqUpdate.setNome("PlayStation Aggiornata");

		piattaformaService.aggiorna(reqUpdate);

		// Verifico l'aggiornamento
		PiattaformaDTO updated = piattaformaService.getById(piattaforma.getId());
		assertEquals("PlayStation Aggiornata", updated.getNome());
		assertTrue(updated.getAttivo(), "La piattaforma aggiornata deve rimanere attiva");
	}

	@Test
	void testAggiornaNonEsistente() {
		PiattaformaReq req = new PiattaformaReq();

		// Id che non esiste
		req.setId(99);
		req.setNome("FakePiattaforma");

		assertThrows(AcademyException.class, () -> piattaformaService.aggiorna(req));
	}

	@Test
	void testDisattivaSuccesso() throws AcademyException {
		PiattaformaReq req = new PiattaformaReq();

		// Supponiamo che esista una piattaforma con id 1
		req.setId(1);
		piattaformaService.disattiva(req);

		// Verifico che la piattaforma sia stata disattivata correttamente
		assertFalse(cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, req.getId()));
	}

	@Test
	void testDisattivaNonEsistenteLanciaEccezione() {
		PiattaformaReq req = new PiattaformaReq();

		// Id inesistente
		req.setId(99);
		assertThrows(AcademyException.class, () -> piattaformaService.disattiva(req));
	}

	@Test
	void testListActive() {
		List<PiattaformaDTO> lista = piattaformaService.listActive();

		// Verifico che la lista non sia vuota
		assertFalse(lista.isEmpty());

		// Verifico che tutte le piattaforme siano attive
		assertTrue(lista.stream().allMatch(PiattaformaDTO::getAttivo));
	}
}
