package com.betacom.retrogames;

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
import com.betacom.retrogames.cache.implementations.CachedPiattaforma;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Piattaforma;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.service.interfaces.PiattaformaService;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Transactional
@SpringBootTest
public class PiattaformaControllerTest 
{
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PiattaformaService piattaformaService;

    @Autowired
    private PiattaformaRepository piattaformaRepo;

    @Test
    void testCreaSuccesso() throws AcademyException 
    {
        PiattaformaReq req = new PiattaformaReq();
        String nomePiattaforma = "PlayStation 5";

        // Normalizzo il nome per evitare discrepanze case-sensitive
        req.setNome(nomePiattaforma);
        req.setCodice("ps1");
        req.setAnnoUscitaPiattaforma(1994);
        req.setAttivo(true);

        // Verifico che la piattaforma non esista già
        Optional<Piattaforma> piattaformaEsistente = piattaformaRepo.findByCodice(req.getCodice());
        if (piattaformaEsistente.isPresent()) 
        {
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
    void testAggiornaSuccesso() throws AcademyException 
    {
        Optional<Piattaforma> piattaformaOpt = piattaformaRepo.findByCodice("PS5");

        // Verifico che la piattaforma esista prima di tentare di aggiornarla
        assertTrue(piattaformaOpt.isPresent(), "La piattaforma 'ps1' non esiste nel database.");

        Piattaforma piattaforma = piattaformaOpt.orElseThrow();  // Ottieni la piattaforma
        cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.PIATTAFORMA, new CachedPiattaforma(piattaforma));

        PiattaformaReq req = new PiattaformaReq();
        req.setId(piattaforma.getId());
        req.setNome("PlayStation Aggiornata");

        // Aggiorno la piattaforma con il nuovo nome
        piattaformaService.aggiorna(req);  

        PiattaformaDTO updated = piattaformaService.getById(piattaforma.getId());  
        assertEquals("PlayStation Aggiornata", updated.getNome());  
    }

    @Test
    void testAggiornaNonEsistente() 
    {
        PiattaformaReq req = new PiattaformaReq();
        
        // Id che non esiste
        req.setId(99);  
        req.setNome("FakePiattaforma");

        assertThrows(AcademyException.class, () -> piattaformaService.aggiorna(req));
    }

    @Test
    void testDisattivaSuccesso() throws AcademyException 
    {
        PiattaformaReq req = new PiattaformaReq();

        // Supponiamo che esista una piattaforma con id 1
        req.setId(1);
        piattaformaService.disattiva(req);

        // Verifico che la piattaforma sia stata disattivata correttamente
        assertFalse(cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, req.getId()));
    }

    @Test
    void testDisattivaNonEsistenteLanciaEccezione() 
    {
        PiattaformaReq req = new PiattaformaReq();

        // Id inesistente
        req.setId(99);
        assertThrows(AcademyException.class, () -> piattaformaService.disattiva(req));
    }

    @Test
    void testListActive() 
    {
        List<PiattaformaDTO> lista = piattaformaService.listActive();

        // Verifico che la lista non sia vuota
        assertFalse(lista.isEmpty());

        // Verifico che tutte le piattaforme siano attive
        assertTrue(lista.stream().allMatch(PiattaformaDTO::getAttivo));
    }
}
